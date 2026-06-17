# Checkmate

> AI 기반 과제 자동 채점 서비스

<br>

## 📌 서비스 소개

**Checkmate**는 교사와 학생을 위한 과제 제출 및 AI 자동 채점 플랫폼입니다.

- 교사는 과제를 등록하고 AI 채점 결과를 확인할 수 있습니다.
- 학생은 과제를 제출하고 채점 결과 및 피드백을 받을 수 있습니다.
- AWS Lambda + Amazon Bedrock 기반 AI가 제출물을 자동으로 채점합니다.

<br>

## 주요 기능

| 기능 | 설명 |
|------|------|
| 과제 등록 | 교사가 과제 및 채점 기준 등록 |
| 과제 제출 | 학생이 파일(이미지) 업로드 후 제출 |
| AI 자동 채점 | Lambda + Bedrock이 제출물 분석 및 채점 |
| 결과 확인 | 채점 결과 및 피드백 조회 |
| 인증/인가 | JWT 기반 로그인, 교사/학생 역할 분리 |
| 과제 분석 | 많이 틀린 문제 , 취약 문제 분석|

<br>

## 아키텍처
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/3af3dcc3-fb1b-450f-83fd-f3c34827e50c" />


**채점 흐름**
1. EC2(Spring Boot) → Lambda **Invoke** (채점 요청)
2. Lambda → S3 **PutObject** (채점 결과 이미지 업로드)
3. Lambda → EC2 **Callback** (채점 결과 전달)




<br>


##  기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white)

### Database
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

### Infra
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat&logo=amazon-ec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=flat&logo=amazon-rds&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=flat&logo=amazon-s3&logoColor=white)
![AWS Lambda](https://img.shields.io/badge/AWS_Lambda-FF9900?style=flat&logo=aws-lambda&logoColor=white)
![Amazon Bedrock](https://img.shields.io/badge/Amazon_Bedrock-9B59B6?style=flat&logo=amazon-aws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white)

### CI/CD
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=github-actions&logoColor=white)
![Docker Hub](https://img.shields.io/badge/Docker_Hub-2496ED?style=flat&logo=docker&logoColor=white)


```
