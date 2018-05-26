import Vue from 'vue'
import Router from 'vue-router'
import Upload from '@/components/Upload'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Upload',
      component: Upload
    }
  ]
})
