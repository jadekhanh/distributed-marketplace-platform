#!/bin/bash

API_URL="http://localhost:8080/graphql"
REQUESTS=100

echo "Sending $REQUESTS product query requests..."

for i in $(seq 1 $REQUESTS)
do
  curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{
      "query": "query { products { id name price quantity } }"
    }' > /dev/null &

  echo "Request $i sent"
done

wait

echo "Load test complete!"