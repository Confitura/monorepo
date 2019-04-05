import { configure } from '@storybook/vue';

import Vue from 'vue';
import Vuex from 'vuex'; // Vue plugins

import Tweet from '../src/components/Tweet.vue';
import '../src/styles.scss';
Vue.use(Vuex);
Vue.component('Tweet', Tweet);


function loadStories() {
  const req = require.context('../src/', true, /.stories.ts$/);
  req.keys().forEach(filename => req(filename));
}

configure(loadStories, module);
