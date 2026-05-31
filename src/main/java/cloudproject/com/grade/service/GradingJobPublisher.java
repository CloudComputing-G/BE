package cloudproject.com.grade.service;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.assignment.domain.Question;
import cloudproject.com.grade.domain.Submission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradingJobPublisher {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.lambda.grading-function-name:}")
    private String gradingFunctionName;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${app.grading.callback-base-url:${swagger.server.url:http://localhost:8080}}")
    private String callbackBaseUrl;

    public void publish(Submission submission) {
        if (!StringUtils.hasText(gradingFunctionName)) {
            log.warn("Skipping AI grading invoke because cloud.aws.lambda.grading-function-name is empty");
            return;
        }

        Map<String, Object> payload = buildPayload(submission);
        Runnable invoke = () -> invokeLambda(submission.getSubmissionId(), payload);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        invoke.run();
                    } catch (RuntimeException e) {
                        log.error("Failed to invoke grading Lambda after commit", e);
                    }
                }
            });
            return;
        }

        invoke.run();
    }

    private void invokeLambda(Long submissionId, Map<String, Object> payload) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(payload);
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(gradingFunctionName)
                    .invocationType(InvocationType.EVENT)
                    .payload(SdkBytes.fromByteArray(json))
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            log.info(
                    "Invoked grading Lambda: submissionId={} function={} httpStatus={} requestId={}",
                    submissionId,
                    gradingFunctionName,
                    response.sdkHttpResponse().statusCode(),
                    response.responseMetadata().requestId()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize grading Lambda payload", e);
        }
    }

    private Map<String, Object> buildPayload(Submission submission) {
        Assignment assignment = submission.getAssignment();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("jobType", "GRADE");
        root.put("submission", buildSubmissionPayload(submission));
        root.put("assignment", buildAssignmentPayload(assignment));
        root.put("questions", buildQuestionPayloads(assignment.getQuestions()));
        root.put("callback", Map.of("url", callbackUrl(submission.getSubmissionId())));
        return root;
    }

    private Map<String, Object> buildSubmissionPayload(Submission submission) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("submissionId", submission.getSubmissionId());
        payload.put("studentId", submission.getStudent().getUserId());
        payload.put("studentName", submission.getStudent().getName());

        if (StringUtils.hasText(submission.getS3Key())) {
            payload.put("image", Map.of(
                    "bucket", bucket,
                    "key", submission.getS3Key(),
                    "contentType", contentType(submission.getS3Key())
            ));
        }
        return payload;
    }

    private Map<String, Object> buildAssignmentPayload(Assignment assignment) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("assignmentId", assignment.getAssignmentId());
        payload.put("title", assignment.getTitle());
        payload.put("subject", assignment.getSubject());
        if (StringUtils.hasText(assignment.getAnswerS3Key())) {
            payload.put("answer", Map.of(
                    "bucket", bucket,
                    "key", assignment.getAnswerS3Key(),
                    "contentType", contentType(assignment.getAnswerS3Key())
            ));
        }
        return payload;
    }

    private List<Map<String, Object>> buildQuestionPayloads(List<Question> questions) {
        List<Question> sorted = new ArrayList<>(questions);
        sorted.sort(Comparator
                .comparing(Question::getOrderNum, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(Question::getQuestionId, Comparator.nullsLast(Long::compareTo)));

        List<Map<String, Object>> payloads = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Question question = sorted.get(i);
            int orderNum = question.getOrderNum() == null ? i + 1 : question.getOrderNum();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("questionId", question.getQuestionId());
            payload.put("orderNum", orderNum);
            payload.put("type", "short_answer");
            payload.put("questionContent", valueOrEmpty(question.getContent()));
            payload.put("expectedAnswer", valueOrEmpty(question.getAnswer()));
            payload.put("maxScore", question.getMaxScore() == null ? 0 : question.getMaxScore());
            payload.put("gradingCriteria", valueOrEmpty(question.getGradingCriteria()));
            payloads.add(payload);
        }
        return payloads;
    }

    private String callbackUrl(Long submissionId) {
        return trimTrailingSlash(callbackBaseUrl)
                + "/api/internal/submissions/"
                + submissionId
                + "/result";
    }

    private String contentType(String s3Key) {
        String lower = s3Key.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".heic")) {
            return "image/heic";
        }
        return "image/jpeg";
    }

    private String trimTrailingSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
