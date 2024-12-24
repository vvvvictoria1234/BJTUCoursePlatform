<template>
  <div class="exam-view">
    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-select v-model="searchForm.courseId" placeholder="请选择课程" style="width: 200px">
        <el-option
            v-for="course in courseList"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
        />
      </el-select>
      <el-button type="primary" @click="loadExamList" style="margin-left: 10px">查询</el-button>
    </div>

    <!-- 考试列表 -->
    <el-table :data="examList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="title" label="考试标题" width="180" />
      <el-table-column prop="description" label="考试说明" show-overflow-tooltip />
      <el-table-column prop="examDate" label="考试日期" width="120" />
      <el-table-column prop="startTime" label="开始时间" width="100" />
      <el-table-column prop="endTime" label="结束时间" width="100" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="viewExamDetail(scope.row)"
          >查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 考试详情对话框 -->
    <el-dialog
        title="考试详情"
        :visible.sync="examDetailDialog.visible"
        width="60%"
    >
      <div v-if="examDetailDialog.examInfo" class="exam-detail">
        <div class="detail-item">
          <span class="label">考试标题：</span>
          <span>{{ examDetailDialog.examInfo.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">考试说明：</span>
          <span>{{ examDetailDialog.examInfo.description }}</span>
        </div>
        <div class="detail-item">
          <span class="label">考试时间：</span>
          <span>{{ examDetailDialog.examInfo.examDate }} {{ examDetailDialog.examInfo.startTime }} - {{ examDetailDialog.examInfo.endTime }}</span>
        </div>
        <div class="detail-item" v-if="examDetailDialog.seatingInfo">
          <span class="label">考场信息：</span>
          <span>{{ examDetailDialog.seatingInfo.buildingName }} {{ examDetailDialog.seatingInfo.roomNumber }}</span>
        </div>
        <div class="detail-item" v-if="examDetailDialog.seatingInfo">
          <span class="label">座位信息：</span>
          <span>第{{ examDetailDialog.seatingInfo.seatRow }}行 第{{ examDetailDialog.seatingInfo.seatColumn }}列</span>
        </div>

      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'StudentExamView',
  data() {
    return {
      // 学生信息
      studentInfo: {
        id: '',
        name: ''
      },
      // 搜索表单
      searchForm: {
        courseId: ''
      },
      // 课程列表
      courseList: [],
      // 考试列表
      examList: [],
      // 考试详情对话框
      examDetailDialog: {
        visible: false,
        examInfo: null,
        seatingInfo: null
      }
    }
  },

  created() {
    this.loadUserInfo()
    this.loadCourseList()
    this.loadExamList()
  },

  methods: {
    // 加载用户信息
    loadUserInfo() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        this.studentInfo = JSON.parse(userStr)
      }
    },

    // 加载课程列表
    async loadCourseList() {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/course/student/courses/' + this.studentInfo.id)
        if (res.data.code === 200) {
          this.courseList = res.data.data
        } else {
          this.$message.error('获取课程列表失败')
        }
      } catch (error) {
        console.error('加载课程列表失败:', error)
        this.$message.error('获取课程列表失败')
      }
    },

    // 加载考试列表
    async loadExamList() {
      try {
        // 获取所有选课信息
        const coursesRes = await this.$axios.get(this.$httpUrl + '/course/student/courses/' + this.studentInfo.id)
        if (coursesRes.data.code !== 200) {
          this.$message.error('获取课程列表失败')
          return
        }

        this.courseList = coursesRes.data.data

        // 如果指定了课程ID，就只获取该课程的考试
        if (this.searchForm.courseId) {
          const res = await this.$axios.get(this.$httpUrl + '/exam/course/' + this.searchForm.courseId)
          if (res.data.code === 200) {
            this.examList = res.data.data
          }
        } else {
          // 否则获取所有课程的考试
          const examPromises = this.courseList.map(course =>
              this.$axios.get(this.$httpUrl + '/exam/course/' + course.courseId)
          )
          const results = await Promise.all(examPromises)
          this.examList = results.reduce((acc, res) => {
            if (res.data.code === 200) {
              return acc.concat(res.data.data)
            }
            return acc
          }, [])
        }
      } catch (error) {
        console.error('加载考试列表失败:', error)
        this.$message.error('获取考试列表失败')
      }
    },

    // 查看考试详情
    async viewExamDetail(exam) {
      try {
        // 先获取考试的所有考场安排
        const arrangementsRes = await this.$axios.get(this.$httpUrl + '/exam/arrangement/' + exam.examId)
        if (arrangementsRes.data.code === 200) {
          const arrangements = arrangementsRes.data.data

          // 获取学生的座位信息
          const seatingRes = await this.$axios.get(this.$httpUrl + '/exam/seating/student', {
            params: {
              studentId: this.studentInfo.id,
              examId: exam.examId
            }
          })

          if (seatingRes.data.code === 200) {
            const seatingInfo = seatingRes.data.data
            // 找到对应的考场安排
            const arrangement = arrangements.find(arr => arr.arrangementId === seatingInfo.arrangementId)
            if (arrangement) {
              // 合并考场和座位信息
              this.examDetailDialog.seatingInfo = {
                ...seatingInfo,
                buildingName: arrangement.buildingName,
                roomNumber: arrangement.roomNumber,
              }
            }
          }
        }

        this.examDetailDialog.examInfo = exam
        this.examDetailDialog.visible = true
      } catch (error) {
        console.error('获取考试详情失败:', error)
        this.$message.error('获取考试详情失败')
      }
    },

    // 获取状态样式
    getStatusType(status) {
      const types = {
        0: 'info',    // 未开始
        1: 'success', // 进行中
        2: 'danger'   // 已结束
      }
      return types[status] || 'info'
    },

    // 获取状态文本
    getStatusText(status) {
      const texts = {
        0: '未开始',
        1: '进行中',
        2: '已结束'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.exam-view {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.exam-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
  line-height: 1.5;
}

.detail-item .label {
  font-weight: bold;
  margin-right: 10px;
  color: #606266;
}
</style>