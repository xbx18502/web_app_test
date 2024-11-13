<template>
  <div class="main-content">
    <div style="display: flex;grid-gap: 10px">

      <div style="flex:1">
        <div class="card" style="padding: 30px; margin-bottom: 10px">
          <div style="font-weight: bold; font-size: 24px; margin-bottom: 20px">{{blog.title}}</div>

          <div style="color: #666666; margin-bottom: 20px">
            <span style="margin-right: 20px"><i class="el-icon-user"></i>{{blog.userName}}</span>
            <span style="margin-right: 20px"><i class="el-icon-date"></i>{{blog.date}}</span>
            <span style="margin-right: 20px"><i class="el-icon-view"></i>{{blog.readCount}}</span>
            <span>
              <el-tag v-for="item in tagsArr" :key="item" type="primary" style="margin-right: 5px">{{item}}</el-tag>
            </span>
          </div>

          <div class="w-e-text">
            <div v-html="blog.content"></div>
          </div>



        </div>

        <div class="card" style="text-align: center;font-size: 20px;color: #666666; margin-bottom: 10px">
          <span style="margin-right: 20px;cursor: pointer"><i class="el-icon-thumb"></i> 10</span>
          <span style="margin-right: 20px;cursor: pointer"><i class="el-icon-star-off"></i> 10</span>
        </div>

        <div class="card">

        </div>

      </div>

      <div style="width: 260px">
        <div class="card" style="margin-bottom: 10px">
          <div style="display: flex; align-items: center; grid-gap: 20px;margin-bottom: 10px">
            <img :src="blog.user?.avatar" alt="" style="width: 50px;height: 50px; border-radius: 50%">
            <div style="flex: 1;">
              <div style="font-weight: bold; margin-bottom: 5px">{{blog.user?.name}}</div>
              <div style="color: #666666; font-size: 13px" class="line2">{{blog.user?.info}}</div>
            </div>
          </div>

          <div style="display: flex">
            <div style="flex: 1; text-align: center">
              <div style="margin-bottom: 5px">article</div>
              <div>10</div>

            </div>
            <div style="flex: 1;text-align: center">
              <div style="margin-bottom: 5px">like</div>
              <div>3</div>
            </div>
            <div style="flex: 1;text-align: center">
              <div style="margin-bottom: 5px">favorite</div>
              <div>10</div>
            </div>
          </div>


        </div>

        <div class="card" style="margin-bottom: 10px">
          <div style="font-weight: bold; font-size: 20px;padding-bottom: 10px;
          border-bottom: 1px solid #dddddd; margin-bottom: 10px">
            Explore
          </div>
          <div>
            <div style="margin-bottom: 15px" v-for="item in recommendList" :key="item.id">
              <div style="margin-bottom: 10px" class="line1">{{item.title}}</div>
              <div style="color: #666666">
                <span style="margin-left: 10px">read</span> <span>{{item.readCount}}</span>
                <span style="margin-left: 10px">likes</span> <span>{{item.likesCount}}</span>
              </div>
            </div>

          </div>
        </div>

        <div class="card">
          <div style="display: flex; grid-gap: 10px">
            <div style="flex: 1;line-height: 10px">
              find your group
            </div>
            <img src="" alt="" style="width: 50px;height: 50px">
          </div>

        </div>

      </div>

    </div>
    <Footer />
  </div>

</template>


<script>
import Footer from "@/components/Footer.vue";
export default {
  name: "BlogDetail",
  components:{
    Footer,
  },
  data(){
    return {
      blogId: this.$route.query.blogId,
      blog:{},
      tagsArr:{},
      recommendList: [
        //{title:'your code is awful'}
      ],
    }
  },
  created(){
    this.load()
  },
  methods:{
    load(){
      this.$request.get('/blog/selectById/' + this.blogId).then(res=>{
        this.blog=res.data || {}
        this.tagsArr=JSON.parse(this.blog.tags || '[]')
      })

      this.$request.get('/blog/selectRecommend/' + this.blogId).then(res=>{
        this.recommendList =res.data || []
      })
    }
  }
}
</script>

<style>
/* blockquote 样式 */
blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 20px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}
pre code {
  display: block;
}
p {
  line-height: 30px
}
</style>