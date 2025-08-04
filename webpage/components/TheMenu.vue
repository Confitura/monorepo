<template>
  <div class="menu">
    <div v-for="item in items" :key="item.label" class="menu-item">
      <template v-if="isVisible(item)">
        <NuxtLink
          v-if="item.url"
          :to="item.url"
          class="menu-link"
          @click.native="click()"
        >{{ item.label }}
        </NuxtLink>
        <span v-else class="menu-link" @click="item.action()">{{
            item.label
          }}</span>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">

let items: MenuItem[] = [
  // { label: 'scanner', url: '/new-scanner', visible: () => isVolunteer() },
  { label: 'home', url: '/#home' },
  { label: 'about us', url: '/about' },
  // { label: 'vote', url: 'https://app.confitura.pl/vote-for-papers' },
  { label: '2023', url: 'http://2023.confitura.pl/' },
  { label: 'login', url: 'https://app.confitura.pl/' },
  // { label: 'tickets', url: '/tickets' },
  // { label: 'partners', url: '/partners' },
  // { label: 'schedule', url: '/schedule' },
  // { label: 'presentations', url: '/presentations' },
  // { label: 'lean coffee', url: '/lean-coffee' },
  // { label: 'speakers', url: '/speakers' },
  // { label: "workshop day", url: "/workshops" },
  // { label: 'FAQ', url: '/faq' },
  // { label: 'my profile', url: '/profile', visible: () => isLogin() },
  // { label: 'ADMIN', url: '/admin', visible: () => isAdmin() },
]

function isVisible(item: MenuItem): boolean {
  return item.visible === undefined || item.visible()
}

const emit = defineEmits(['linkClicked'])
function click() {
  emit('linkClicked')
}


interface MenuItem {
  label: string;
  url?: string;
  action?: () => void;
  visible?: () => boolean;
}
</script>

<style scoped lang="scss">
@use "~/assets/colors" as *;
@use "~/assets/media" as *;

.menu {
  flex-basis: 170px;
  align-self: flex-end;
  height: 100%;

  &.header--default,
  &.header--white,
  &.header--red,
  &.header--black {
    background-color: #000000;
    @include lg() {
      background-color: transparent;
    }
  }

  @include lg() {
    flex-basis: unset;
    justify-content: right;
    display: unset;
  }

  .menu-item {
    margin: 2rem;

    @include lg() {
      margin: 0.4rem;
      display: unset;
    }
  }

  & .menu-link {
    text-decoration: none;
    color: $brand;
    font-size: 1.1rem;
    cursor: pointer;

    @include xl() {
      font-size: 1.3rem;
    }

    &:hover,
    &.router-link-active {
      color: #ffffff;
      border-bottom: 2px #ffffff solid;

      .header--red &,
      .header--black & {
        color: #000000;
        border-bottom: 2px #000000 solid;
      }
    }
  }
}
</style>
