<template>
  <div class="main-content">
    <div style="display: flex; align-items: flex-start; grid-gap: 10px">

      <div style="width: 150px" class="card">
        <div class="category-item" :class="{ 'category-item-active': item.name === current }"
             v-for="item in categoryList" :key="item.id" @click="selectCategory(item.name)">{{ item.name }}</div>
      </div>

      <div style="flex: 1;">
        <div class="card" style="min-height: 80vh">
          <div class="blog-box" v-for="item in tableData" :key="item.id" v-if="total > 0">
            <div style="flex: 1; width: 0">
              <div style="font-size: 16px; font-weight: bold; margin-bottom: 10px">{{ item.title }}</div>
              <div class="line1" style="color: #666; margin-bottom: 10px; font-size: 13px">{{ item.descr }}</div>
              <div style="display: flex">
                <div style="flex: 1; font-size: 13px">
                  <span style="color: #666; margin-right: 20px"><i class="el-icon-user"></i> {{ item.userName }}</span>
                  <span style="color: #666; margin-right: 20px"><i class="el-icon-eye"></i> {{ item.readCount }}</span>
                  <span style="color: #666"><i class="el-icon-like"></i> {{ item.likesCount }}</span>
                </div>
                <div style="width: fit-content">
                  <el-tag type="primary" style="margin-right: 10px">后端</el-tag>
                  <el-tag type="primary">面试</el-tag>
                </div>
              </div>
            </div>
            <div style="width: 150px; height: 80px; display: flex; align-items: center; justify-content: center;">
              <img style="max-width: 100%; max-height: 100%; border-radius: 5px;" :src="item.cover" alt="">
            </div>
          </div>
          <div v-if="total === 0" style="padding: 20px 0; text-align: center; font-size: 16px; color: #666">暂无数据</div>

          <div style="margin-top: 10px" v-if="total">
            <el-pagination
                background
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5, 10, 20]"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total">
            </el-pagination>
          </div>
        </div>

        <Footer />


      </div>

      <div style="width: 260px">
        <div class="card" style="margin-bottom: 10px;">
          <div style="font-size: 22px ; font-weight: bold; margin-bottom: 10px">welcome! >_< </div>
          <a href="/front/person"><div>Write a blog now</div></a>
        </div>
        <div class="card" style="margin-bottom: 10px">
          <div style="display: flex; align-items: baseline;color: #8c939d;padding-bottom: 10px;border-bottom: 1px solid">
            <div style="font-size: 19px;flex: 1">blog list</div>
            <div style="font-size: 12px; color: #8c939d;cursor: pointer;"@click="refreshTop"><i class="el-icon-refresh"></i>refresh</div>
          </div>
          <div style="margin-top: 10px">
            <div v-for="item in showList" :key="item.id" style="margin-bottom: 6px" class="line1">
              <span style="width: 20px;display: inline-block; text-align: right;margin-right: 10px">
                <span style="color: orangered" v-if="item.index===1">{{item.index}}</span>
                <span style="color: gold" v-else-if="item.index===2">{{item.index}}</span>
                <span style="color: blueviolet" v-else-if="item.index===3">{{item.index}}</span>
                <span style="color: green" v-else-if="item.index===4">{{item.index}}</span>
                <span style="color: #8c939d" v-else>{{item.index}}</span>
              </span>
              <span style="color: #8c939d;">{{item.title}}</span>
            </div>

          </div>
        </div>

        <div style="margin-bottom: 10px">
          <div v-for="item in topActivityList" :key="item.id" style="margin-bottom: 10px">
            <a :href="'/front/activityDetail?activityId='  + item.id" target="_blank"><img :src="item.cover" alt="" style="width: 100%;border-radius: 5px"></a>
          </div>
        </div>

        <div style="line-height: 30px; color: #8c939d;padding: 0 10px">
          <div>举报邮箱： feedback@xitu.io</div>
          <div>座机电话： 010-83434395</div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>



import Footer from "@/components/Footer.vue";

export default {
  components:{
    Footer
  },

  data() {
    return {
      current: '全部博客',  //当前选中的分类名称
      categoryList: [],
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      topList:[],
      showList:[], //content of blog list being displayed
      lastIndex:0,
      topActivityList:[],
    }
  },
  mounted() {
    this.load()

    this.loadBlogs(1)
    this.refreshTop()
    this.loadTopActivity()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadTopActivity(){
      this.$request.get('/activity/selectTop').then(res => {
        this.topActivityList = res.data || []
      })
    },
    refreshTop(){
      this.$request.get('/blog/selectTop').then(res => {
        this.topList = res.data || []
        let i=1
        this.topList.forEach(item => item.index=i++)
        if(this.lastIndex >=10){
          this.lastIndex=0
        }
        this.showList=this.topList.slice(this.lastIndex,this.lastIndex+5)
        this.lastIndex+=5
      })
    },
    selectCategory(categoryName) {
      this.current = categoryName

      this.loadBlogs(1)
    },
    load() {
      // 请求分类的数据
      this.$request.get('/category/selectAll').then(res => {
        this.categoryList = res.data || []
        this.categoryList.unshift({ name: '全部博客' })
      })
    },
    loadBlogs(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/blog/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          categoryName: this.current === '全部博客' ? null : this.current,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style>
.category-item {
  text-align: center;
  padding: 10px 0;
  font-size: 16px;
  cursor: pointer;
}
.category-item-active {
  background-color: #1890ff;
  color: #fff;
  border-radius: 5px;
}
.line1 {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.blog-box {
  display: flex;
  grid-gap: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #ddd;
}
.blog-box:first-child {
  padding-top: 0;
}
</style>