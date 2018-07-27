#!/usr/bin/env bash

echo "Standing up fileuploader application"
docker run -d \
    --network lcag-automation-network \
    -e "SPRING_PROFILES_ACTIVE=prod" \
	-e "SMTP_HOST=lcag-mail" \
	-e "SMTP_PORT=3025" \
	-e "SMTP_USERNAME=lcag-testing@lcag.com" \
	-e 'SMTP_PASSWORD=password' \
	-e "SFTP_USERNAME=user" \
	-e 'SFTP_PASSWORD=password' \
    -e "SFTP_HOST=lcag-sftp" \
	-e "SFTP_PORT=22" \
	-e "SFTP_ROOT_DIRECTORY=/upload" \
	-e "DOCUMENT_UPLOAD_DESTINATION_DIRECTORY=/tmp" \
	-e "MYBB_FORUM_DATABASE_URL=jdbc:mysql://lcag-mysql/mybb" \
	-e "MYBB_FORUM_DATABASE_USERNAME=root" \
	-e "MYBB_FORUM_DATABASE_PASSWORD=p@ssword" \
	-e "EMAIL_SOURCE_URL=https://docs.google.com/document/d/1TgUi_IG4tdolfoXzPmPOoUCqCxGuc7y6P5D7bA13Kus/export?format=html" \
	-e "BCC_RECIPIENTS=test@bcc.com" \
	-e "EMAIL_FROM_NAME=LCAG" \
	-e "EMAIL_SUBJECT=Your LCAG Membership Application" \
	-e "VIRTUAL_PORT=8383" \
	-e "SERVER_PORT=8383" \
	--name fileuploader \
    -p 8383:8383 \
    -t dockernovinet/fileuploader

echo "Waiting for application status url to respond with 200"
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8383)" != "200" ]]; do sleep 5; done