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

const {type = 'twitter', id, theme = 'black'} = defineProps<{
  type: SocialType,
  id: string | undefined,
  theme?: string
}>()

type SocialType = 'twitter' | 'facebook' | 'github' | 'www'

const social: Record<SocialType, { icon: string; url: string }> = {
  twitter: {
    icon: 'fa-brands fa-square-x-twitter',
    url: 'https://x.com/'
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
let url = computed(() => {
  if (type == 'www' && !id.startsWith('http')) {
    return 'https://' + id
  }
  return social[type].url + id

})
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
