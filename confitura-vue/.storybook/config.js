import { configure } from '@storybook/vue';

import Vue from 'vue';
import Vuex from 'vuex'; // Vue plugins

import Twit from '../src/components/Twit.vue';
import '../src/styles.scss';
Vue.use(Vuex);
Vue.component('Twit', Twit);


function loadStories() {
  const req = require.context('../src/', true, /.stories.ts$/);
  req.keys().forEach(filename => req(filename));
}

configure(loadStories, module);
