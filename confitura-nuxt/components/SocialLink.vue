<template>
  <a
    :href="url"
    v-if="id"
    target="_blank"
    rel="noopener"
    class="link"
    :class="`link--${theme}`"
  >
    <i :class="icon"></i>
  </a>
</template>

<script setup lang="ts">

const { type = 'twitter', id, theme = 'black' } = defineProps<{ type: string, id: string, theme?: string }>()

const social = {
  twitter: {
    icon: 'fab fa-twitter-square',
    url: 'https://twitter.com/'
  },
  facebook: {
    icon: 'fab fa-facebook-square',
    url: 'https://www.facebook.com/'
  },
  github: {
    icon: 'fab fa-github-square',
    url: 'https://www.github.com/'
  },
  www: {
    icon: 'fas fa-globe',
    url: ''
  }
}

let icon = computed(() => social[type].icon)
let url = computed(() => social[type].url + id)
</script>
<style scoped lang="scss">
@use "~/assets/colors" as *;

.link {
  &#{&}--black {
    color: #000000;
  }

  &#{&}--white {
    color: #ffffff;
  }

  &#{&}:hover {
    color: $brand;
  }
}
</style>
