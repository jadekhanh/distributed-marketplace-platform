#!/bin/bash

API_URL="http://localhost:8080/graphql"

echo "Creating buyer user..."

curl -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { register(input: { email: \"milu@plushies.com\", password: \"miluisfeelingnaughty!\", firstName: \"Milu\", lastName: \"Tran\", role: BUYER }) { token email roles } }"
  }'

echo ""
echo "Creating seller user..."

curl -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { register(input: { email: \"ducko@plushies.com\", password: \"duckoisveryhungry?\", firstName: \"Ducko\", lastName: \"Tran\", role: SELLER }) { token email roles } }"
  }'

echo ""
echo "Done and easy!"