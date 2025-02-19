#!/bin/bash

# 변수 설정
KEY_FILE="~/.ssh/I12A802T.pem"
SERVER="i12a802.p.ssafy.io"

# 빌드: Java 안깔린 경우 intellij gradle 탭에서 수동 빌드
#./gradlew build
./gradlew clean
./gradlew bootJar

# jar 파일 이름 변경
mv ./build/libs/tolonbgeub-0.0.1-SNAPSHOT.jar ./build/libs/app.jar

ssh -i ${KEY_FILE} ubuntu@${SERVER} "
sudo rm app.jar
sudo rm .env
"

# 1. jar 파일 전송
scp -i ${KEY_FILE} ./build/libs/app.jar ubuntu@${SERVER}:~

# 2. .env 파일 전송
scp -i ${KEY_FILE} .env ubuntu@${SERVER}:~

# 3. 원격 서버에서 기존 프로세스 종료 후 새로운 jar 실행
ssh -i ${KEY_FILE} ubuntu@${SERVER} "
  # 현재 실행 중인 Java 프로세스 중 app.jar 이름이 있는 프로세스를 찾아 종료
  PID=\$(ps -ef | grep 'java -jar' | grep 'app.jar' | grep -v grep | awk '{print \$2}')

  if [ ! -z \"\$PID\" ]; then
    echo \"Stopping existing server with PID \$PID\"
    sudo kill \$PID

    # 프로세스가 완전히 종료될 때까지 짧게 대기
    sleep 3

    # 만약 정상적으로 종료되지 않았다면 강제 종료
    if ps -p \$PID > /dev/null; then
      echo \"Force stopping server with PID \$PID\"
      sudo kill -9 \$PID
    fi
  else
    echo \"No existing server found running\"
  fi

  # .env 파일에서 환경변수 로드
  export \$(grep -v '^#' ~/.env | xargs)

  echo \"Starting new server...\"
  # 환경변수를 적용하여 jar 파일을 백그라운드에서 실행
  # sudo -E nohup java -jar ~/app.jar > ~/app.log 2>&1 &
  sudo java -jar ~/app.jar

  echo \"Server started with PID \$!\"
"