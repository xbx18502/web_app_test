#!/bin/bash

# 1. Login and get token
TOKEN=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"xbxh","password":"123","role":"USER"}' \
  http://localhost:9090/login | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "Failed to get token"
    exit 1
fi

echo "Token obtained: $TOKEN"

# 2. Create temp file for ab headers
echo "token: $TOKEN" > headers.txt
echo "Content-Type: application/json" >> headers.txt

# 3. Run ab with token
ab -n 1000 -c 100 \
   -H "token: $TOKEN" \
   -H "Content-Type: application/json" \
   http://127.0.0.1:9090/blog/selectById/18

# 4. Cleanup
rm headers.txt
