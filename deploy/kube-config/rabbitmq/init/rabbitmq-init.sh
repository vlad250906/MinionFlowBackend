#!/bin/sh

curl -Ss --fail-with-body \
  --retry 60 --retry-connrefused --retry-all-errors --retry-delay 1 \
  -u "${RABBITMQ_USER}:${RABBITMQ_PASSWORD}" \
  -H "content-type: application/json" \
  -X POST "http://rabbitmq:15672/api/definitions" \
  --data-binary "@/work/definitions.json" \
  -w "\nHTTP %{http_code}\n"