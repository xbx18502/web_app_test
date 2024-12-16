peixinxiao@Peixins-MacBook-Pro Stress_Testing % ./ab.sh
Token obtained: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxLVVTRVIiLCJleHAiOjE3MzQzNDU3ODd9.7sUWVt2JNMuxb7Kqfxl9khkDIcNv-tp2KmXYanzpeEw
This is ApacheBench, Version 2.3 <$Revision: 1913912 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 800 requests
Completed 1600 requests
Completed 2400 requests
Completed 3200 requests
Completed 4000 requests
Completed 4800 requests
Completed 5600 requests
Completed 6400 requests
Completed 7200 requests
Completed 8000 requests
Finished 8000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            9090

Document Path:          /blog/selectAll
Document Length:        43641 bytes

Concurrency Level:      100
Time taken for tests:   6.322 seconds
Complete requests:      8000
Failed requests:        0
Total transferred:      350680000 bytes
HTML transferred:       349128000 bytes
Requests per second:    1265.34 [#/sec] (mean)
Time per request:       79.030 [ms] (mean)
Time per request:       0.790 [ms] (mean, across all concurrent requests)
Transfer rate:          54166.06 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0      10
Processing:     3   78  35.0     69     337
Waiting:        3   78  35.0     69     337
Total:          3   78  35.1     70     337

Percentage of the requests served within a certain time (ms)
  50%     70
  66%     78
  75%     92
  80%    101
  90%    127
  95%    148
  98%    180
  99%    198
 100%    337 (longest request)