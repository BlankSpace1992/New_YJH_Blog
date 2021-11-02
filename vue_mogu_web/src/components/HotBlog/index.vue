<template>
    <div class="tuijian" v-if="hotBlogData.length > 0">
      <h2 class="hometitle">点击排行</h2>
      <ul class="tjpic" v-if="hotBlogData[0]">
        <i><img style="cursor:pointer" v-if="hotBlogData[0].photoList" :src="hotBlogData[0].photoList[0]" @click="goToInfo(hotBlogData[0])"></i>
        <p><a href="javascript:void(0);" @click="goToInfo(hotBlogData[0])">{{hotBlogData[0].title}}</a></p>
      </ul>
      <ul class="sidenews">
        <li v-for="(item, index) in sideNews"  :key="item.uid">
          <i><img style="cursor:pointer"  v-if="item.photoList" :src="item.photoList[0]" @click="goToInfo(item)"></i>
          <p><a href="javascript:void(0);" @click="goToInfo(item)">{{item.title}}</a></p>
          <span>{{dateFormat("YYYY-mm-dd HH:MM:SS",item.createTime)}}</span>
        </li>
      </ul>
    </div>
</template>

<script>
import { getHotBlog } from "../../api/index";
import {getBlogByUid} from "../../api/blogContent";
export default {
  name: "TagCloud",
  data() {
    return {
      hotBlogData: [] //热门博客列表
    };
  },
  created() {
    getHotBlog().then(response => {
      if (response.data.code === this.$ECode.SUCCESS) {
        this.hotBlogData = response.data.result.records;
      }
    });
  },
  computed: {
    //添加一个计算属性用于简单过滤掉数组中第一个数据
    sideNews() {
      return this.hotBlogData.filter(blog =>
        this.hotBlogData.indexOf(blog) !== 0
      )
    }
  },
  methods: {
    // 格式化日期
    dateFormat(fmt,date){
      const dateTime = new Date(date);
      let ret;
      const opt = {
        "Y+": dateTime.getFullYear().toString(),        // 年
        "m+": (dateTime.getMonth() + 1).toString(),     // 月
        "d+": dateTime.getDate().toString(),            // 日
        "H+": dateTime.getHours().toString(),           // 时
        "M+": dateTime.getMinutes().toString(),         // 分
        "S+": dateTime.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
      };
      for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
          fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
      };
      return fmt;
    },
    //跳转到文章详情【或推广链接】
    goToInfo(blog) {
      if(blog.type == "0") {
        let routeData = this.$router.resolve({
          path: "/info",
          query: {blogOid: blog.oid}
        });
        window.open(routeData.href, '_blank');
      } else if(blog.type == "1") {
        var params = new URLSearchParams();
        params.append("uid", blog.uid);
        getBlogByUid(params).then(response => {
          // 记录一下用户点击日志
        });
        window.open(blog.outsideLink, '_blank');
      }
    },
  }
};
</script>

<style>

</style>
