<template>
  <div class="container">
    <div style="width: 390px; padding: 50px; background-color: rgba(255,255,255,.75); border-radius: 5px;">
      <div style="text-align: center; font-size: 24px; margin-bottom: 30px; color: #333">Blog&forum Platform</div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input size="medium" prefix-icon="el-icon-user" placeholder="input username" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium" prefix-icon="el-icon-lock" placeholder="input password" show-password  v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="role">
         <el-radio-group v-model="form.role">
           <el-radio label="ADMIN">Admin</el-radio>
           <el-radio label="USER">User</el-radio>
         </el-radio-group>
        </el-form-item>
        <el-form-item prop="code">
          <div style="display: flex">
            <el-input style="flex: 1" size="medium" v-model="code">
            </el-input>
            <Identify :identifyCode="identifyCode" @click.native ="refreshCode" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button size="medium" style="width: 100%; background-color: #329900; border-color: #333; color: white" @click="login">Log in</el-button>
        </el-form-item>
        <div style="display: flex; align-items: center">
          <div style="flex: 1"></div>
          <div style="flex: 1; text-align: right">
            Don't have any account yet? <a href="/register">sign up here</a>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import Identify from "@/components/Identify.vue";

export default {
  name: "Login",
  components:{
    Identify
  },
  data() {
    return {
      form: { role: 'ADMIN' },
      rules: {
        username: [
          { required: true, message: 'input username', trigger: 'blur' },
        ],
        password: [
          { required: true, message: 'input password', trigger: 'blur' },
        ]
      },
      code: '',   // 表单绑定的验证码
      // 图片验证码
      identifyCode: '',
      // 验证码规则
      identifyCodes: '123456789ABCDEFGHGKMNPQRSTUVWXY',
    }
  },
  created() {

  },
  mounted() {
    this.refreshCode()
  },
  methods: {
    // 切换验证码
    refreshCode() {
      this.identifyCode = ''
      this.makeCode(this.identifyCodes, 4)
    },
    // 生成随机验证码
    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode += this.identifyCodes[Math.floor(Math.random() * (this.identifyCodes.length))]
      }
    },
    login() {
      if(!this.code){
        this.$message.warning("please input identification code")
        this.refreshCode()
        return
      }
      else if(this.code.toLowerCase()!==this.identifyCode.toLowerCase()){
        this.$message.warning("wrong identification code")
        this.refreshCode()
        return
      }
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
              this.$message.success('登录成功')
              setTimeout(() => {
                if(res.data.role==='ADMIN'){
                  location.href='/home'
                }
                else{
                  location.href='/front/home'  // 跳转主页
                }
              }, 500)

            } else {
              this.refreshCode()
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/bg2.png");
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}
a {
  color: #2a60c9;
}
</style>