#!/usr/bin/env bash

echo "Standing up fileuploader application"
docker run -d \
    --network lcag-automation-network \
    -e "SPRING_PROFILES_ACTIVE=prod" \
	-e "SMTP_HOST=lcag-mail" \
	-e "SMTP_PORT=3025" \
	-e "SMTP_USERNAME=lcag-testing@lcag.com" \
	-e 'SMTP_PASSWORD=password' \
	-e "MYBB_FORUM_DATABASE_URL=jdbc:mysql://lcag-mysql/mybb" \
	-e "MYBB_FORUM_DATABASE_USERNAME=root" \
	-e "MYBB_FORUM_DATABASE_PASSWORD=p@ssword" \
	-e "EMAIL_SOURCE_URL=https://docs.google.com/document/d/1xjM-bwA5ljD5rtYP-YEkYtJqVKCmL6kbAFuSGyQoUAc/export?format=html" \
	-e "BCC_RECIPIENTS=test@bcc.com" \
	-e "EMAIL_FROM_NAME=LCAG" \
	-e "EMAIL_SUBJECT=LCAG Claim Participant Confirmation" \
	-e "VIRTUAL_PORT=8484" \
	-e "SERVER_PORT=8484" \
	--name fileuploader \
    -p 8484:8484 \
    -t dockernovinet/fileuploader

echo "Waiting for application status url to respond with 200"
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8484)" != "200" ]]; do sleep 5; done