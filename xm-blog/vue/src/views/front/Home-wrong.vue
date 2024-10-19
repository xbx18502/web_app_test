<template>
  <div class="main-content">
    <div style="display: flex; grid-gap: 10px">
      <div style="width: 150px" class="card">
        <div class="category-item" :class="{'category-item-active': item.name===current}"
             v-for="item in categoryList" :key="item.id" @click="selectCategory(item.name)">{{item.name}}
        </div>
      </div>

      <div style="flex: 1" class="card">
        <div style="display: flex;padding: 10px 0; border-bottom: 1px solid #dddddd; margin-bottom: 10px"
        v-for="item in tableData":key="item.id">
          <div style="flex: 1; width: 0">
            <div style="font-size: 16px; font-weight: bold ;margin-bottom: 10px">{{item.title}}</div>
            <div class="line1" style="color: #000b17;margin-bottom: 10px; font-size: 13px">
              {{item.descr}}
            </div>
            <div style="display: flex">
              <div style="flex: 1">
                <span style="color: #2C0D0DFF ; margin-right: 20px"><i class="el-icon-user"></i>{{item.userName}}</span>
                <span style="color: #2c0d0d; margin-right: 20px"><i class="el-icon-view"></i>{{item.readCount}}</span>
                <span style="color: #2C0D0DFF; margin-right: 20px"><i class="el-icon-top"></i>{{item.likesCount}}</span>
              </div>
              <div style="width: fit-content">
                <el-tag type="primary" style="margin-right: 10px" >beckend</el-tag>
                <el-tag type="primary">interview</el-tag>
              </div>
            </div>
          </div>
          <div style="width: 100px">
            <img style="width: 100%; border-radius: 4px" src="" alt="">
          </div>
        </div>
        <div>
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



      <div style="width: 260px" class="card"></div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    return {
      current: '全部博客', //当前选中的分类名称
      categoryList:[],
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
    }
  },
  mounted() {
    this.load()
    this.loadBlogs(1)
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    selectCategory(categoryName){
      this.current = categoryName
      this.loadBlogs(1)
    },
    load(){
      // request for category
      this.$request.get("/category/selectAll").then(res =>{
        this.categoryList=res.data || []
        this.categoryList.unshift({ name: '全部博客' })
      })

    },
    loadBlogs(pageNum, ){
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/blog/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          // title: this.title,
          categoryName: this.current ==='全部博客' ? null : this.current,
          // userName: this.userName,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
      this.$request.get('/category/selectAll').then( res=> {
        this.categoryList=res.data||[]
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>
<style>
  .category-item{
    text-align:center;
    padding: 10px 0;
    font-size: 16px;
    cursor: pointer;
  }
  .category-item-active{
    background-color: #1890ff;
    color: #ffff00;
    border-radius: 6px;
  }
.line1 {
  white-space: nowrap;
  //width : 80%;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
