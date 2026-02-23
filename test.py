import urllib.request
import json
import sys

url = 'http://localhost:8080/chats/f329fc26-9a5c-4063-abb2-6ed1111d67c5/messages'
headers = {
    'Content-Type': 'application/json',
    'X-Guest-Session-Id': 'test-session-123',
    'Accept': 'text/event-stream'
}
data = json.dumps({'message': 'Hello'}).encode('utf-8')

req = urllib.request.Request(url, data=data, headers=headers)

try:
    with urllib.request.urlopen(req) as response:
        print(f"Status: {response.status}")
        for line in response:
            print("EVENT:", line.decode('utf-8').strip())
except Exception as e:
    print("Error:", e)
