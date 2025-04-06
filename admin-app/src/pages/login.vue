<script setup lang="ts">
import router from "@/plugins/router.ts";

const name = ref('')
const apiUrl = import.meta.env.VITE_API_URL;
const selfUrl = import.meta.env.VITE_SELF_URL;

function sayHi() {
  localStorage.setItem('token', name.value)
  router.push('/')
}

function warning() {
  Notify.warning(`How dare you refuse me, ${name.value}.`)
  localStorage.removeItem('token')
}

definePage({
  meta: {
    icon: 'mdi-home',
    title: 'Login',
    drawerIndex: 0,
    skipMenu: true,
    layout: 'whole-page',
  },
})

function loginUrl(provider: string) {

  const url = new URL(`${apiUrl}/login/${provider}`);
  url.searchParams.set('redirect_uri', `${selfUrl}/login/${provider}`);
  url.searchParams.set('state', "static-state");
  return url.toString();
}
</script>

<template>
  <div class="wrapper">
    <v-icon icon="custom:confitura" size="3.5em" color="primary" class="mb-4"/>
    <p>Register/Login with chosen provider:</p>
    <v-container>
      <v-row align="center" justify="center">
        <v-col cols="auto">
          <v-btn icon="mdi-github" size="x-large"
                 :href="loginUrl('github')"></v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn icon="mdi-google" size="x-large"
                 :href="loginUrl('google')"></v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn icon="mdi-facebook" size="x-large"
                 :href="loginUrl('facebook')"></v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn icon="mdi-alpha-x" size="x-large"
                 :href="loginUrl('twitter')"></v-btn>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.wrapper {
  position: relative;
  top: calc(50vh - 330px);
  text-align: center;
}
</style>
