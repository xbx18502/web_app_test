<template>
  <div>
    <div style="margin-bottom: 15px">
      <el-input style="width: 200px; margin-right: 10px" placeholder="请输入内容"></el-input>
      <el-button type="warning">search</el-button>
      <el-button type="primary">add</el-button>
    </div>
    <div>
      <el-table :data="tableData" style="width: 100%; margin: 15px 0px">
        <el-table-column prop="date" label="data" width="180"></el-table-column>
        <el-table-column prop="name" label="name" width="180"></el-table-column>
        <el-table-column prop="address" label="address"></el-table-column>
        <el-table-column label="operation">
          <el-button type="primary">edit</el-button>
          <el-button type="danger">delete</el-button>
        </el-table-column>
      </el-table>
    </div>
    <div class="block">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage4"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="10"
          layout="total, sizes, prev, pager, next, jumper"
          :total="400">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [{
        date: '2016-05-02',
        name: 'liwanying',
        address: '上海市普陀区金沙江路 1518 弄'
      }, {
        date: '2016-05-04',
        name: 'xiaopeixin',
        address: '上海市普陀区金沙江路 1517 弄'
      }, {
        date: '2016-05-01',
        name: 'c',
        address: '上海市普陀区金沙江路 1519 弄'
      }, {
        date: '2016-05-03',
        name: 'd',
        address: '上海市普陀区金沙江路 1516 弄'
      }]
    }
  }
}
</script>