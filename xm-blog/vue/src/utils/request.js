import axios from 'axios'
import router from "@/router";

// 创建可一个新的axios对象
const request = axios.create({
    baseURL: process.env.VUE_APP_BASEURL,   // 后端的接口地址  ip:port
    timeout: 30000                          // 30s请求超时
})
// 判断token是否过期
function isTokenExpired(token) {
    if (!token) return true;
    const expiry = JSON.parse(atob(token.split('.')[1])).exp;
    return (Math.floor((new Date).getTime() / 1000)) >= expiry;
}
// 刷新token
async function refreshToken() {
    try {
        const response = await axios.post(`${process.env.VUE_APP_BASEURL}/refreshToken`, {}, {
            withCredentials: true // Enable sending cookies
        });
        const { token } = response.data.data;
        const user = JSON.parse(localStorage.getItem("xm-user") || '{}');
        user.token = token;
        localStorage.setItem("xm-user", JSON.stringify(user));
        return token;
    } catch (error) {
        localStorage.removeItem("xm-user");
        router.push('/login');
        return null;
    }
}

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(async config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    config.withCredentials = true; // Enable sending cookies
    let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
    
    if (user.token && isTokenExpired(user.token)) {
        const newToken = await refreshToken();
        if (newToken) {
            user.token = newToken;
        }
    }
    
    config.headers['token'] = user.token;
    return config;
}, error => {
    console.error('request error: ' + error) // for debug
    return Promise.reject(error);
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        if (response.config.responseType === 'blob') {
            return response.data;
        }
        let res = response.data;

        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        if (res.code === '401') {
            router.push('/login');
        }
        return res;
    },
    error => {
        console.error('response error: ' + error) // for debug
        return Promise.reject(error)
    }
)


export default request