<template>
    <div class="write-blog">
        <h2>Write New Blog</h2>
        <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
            <el-form-item label="Title" prop="title">
                <el-input v-model="form.title" placeholder="Enter title"></el-input>
            </el-form-item>

            <el-form-item label="Description" prop="descr">
                <el-input type="textarea" v-model="form.descr" placeholder="Enter brief description"></el-input>
            </el-form-item>

            <el-form-item label="Cover" prop="cover">
                <el-upload :action="$baseUrl + '/files/upload'" :headers="{ token: user.token }" list-type="picture"
                    :on-success="handleCoverSuccess">
                    <el-button type="primary">Upload Cover</el-button>
                </el-upload>
            </el-form-item>

            <el-form-item label="Category" prop="categoryId">
                <el-select v-model="form.categoryId" style="width: 100%">
                    <el-option v-for="item in categoryList" :key="item.id" :value="item.id" :label="item.name">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="标签" prop="tags">
                <el-select v-model="tagsArr" multiple filterable allow-create default-first-option style="width: 100%">
                    <el-option value="后端"></el-option>
                    <el-option value="Java"></el-option>
                    <el-option value="面试"></el-option>
                    <el-option value="Vue"></el-option>
                    <el-option value="前端"></el-option>
                    <el-option value="大数据"></el-option>
                    <el-option value="算法"></el-option>
                    <el-option value="程序员"></el-option>
                    <el-option value="小白"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="Content" prop="content">
                <!-- Add editor container -->
                <div id="editor-container"></div>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="submitBlog">Submit</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import E from 'wangeditor'
import hljs from 'highlight.js'
export default {
    name: 'WriteBlog',
    data() {
        return {
            editor: null,
            form: {
                title: '',
                descr: '',
                content: '',
                cover: '',
                categoryId: ''
            },
            rules: {
                title: [{ required: true, message: 'Please input title', trigger: 'blur' }],
                descr: [{ required: true, message: 'Please input description', trigger: 'blur' }],
                categoryId: [{ required: true, message: 'Please select category', trigger: 'change' }],
                content: [{ required: true, message: 'Please input content', trigger: 'blur' }]
            },
            categoryList: [],
            user: JSON.parse(localStorage.getItem('xm-user') || '{}')
        }
    },
    created() {
        this.loadCategories()
    },
    mounted() {
        // Initialize editor
        this.editor = new E('#editor-container')

        // Configure editor
        this.editor.config.height = 500
        this.editor.config.zIndex = 100

        // Sync content to form data
        this.editor.config.onchange = (newHtml) => {
            this.form.content = newHtml
        }

        // Configure code highlighting
        this.editor.highlight = hljs

        // Create editor
        this.editor.create()
    },
    beforeDestroy() {
        // Destroy editor instance
        if (this.editor) {
            this.editor.destroy()
        }
    },
    methods: {
        loadCategories() {
            this.$request.get('/category/selectAll').then(res => {
                this.categoryList = res.data
            })
        },
        handleCoverSuccess(res) {
            this.form.cover = res.data
        },
        submitBlog() {
            this.$refs.formRef.validate(valid => {
                if (valid) {
                    this.$request.post('/blog/add', this.form).then(res => {
                        if (res.code === '200') {
                            this.$message.success('Blog published successfully')
                            this.$router.push('/front/home')
                        } else {
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
.write-blog {
    padding: 20px;
    max-width: 1000px;
    margin: 0 auto;
}

/* Style for editor container */
#editor-container {
    border: 1px solid #ccc;
    z-index: 100;
}
</style>