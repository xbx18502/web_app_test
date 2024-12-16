-- auth.lua

-- Initialize the counter for requests
counter = 0

-- Initialize request method
wrk.method = "GET"

-- Initialize headers as a table
wrk.headers = {}
wrk.headers["Content-Type"] = "application/json"

-- Setup function runs at the beginning of the test
function setup(thread)
    -- Store the base URL
    thread:set("base_url", "http://localhost:9090")
    
    -- Perform login
    local login_body = '{"username":"xbxh","password":"123","role":"USER"}'
    local headers = {
        ["Content-Type"] = "application/json"
    }
    
    -- Create login request
    local login_path = "/login"
    local r = wrk.format("POST", thread:get("base_url") .. login_path, headers, login_body)
    
    -- Send login request
    local response = wrk.lookup(thread):request(r)
    
    if response.status == 200 then
        -- Extract token from response
        local token = string.match(response.body, '"token":"([^"]+)"')
        if token then
            thread:set("token", token)
            print("Successfully obtained token")
        else
            print("Failed to extract token from response")
        end
    else
        print("Login failed with status: " .. response.status)
    end
end

-- Request function runs for each request
function request()
    local token = wrk.thread:get("token")
    if token then
        wrk.headers["token"] = token
    end
    return wrk.format()
end

-- Response function handles each response
function response(status, headers, body)
    if status ~= 200 then
        print("Request failed with status: " .. status)
    end
end