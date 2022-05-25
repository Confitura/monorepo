<template>
  <div class="menu" :key="isLogin">
    <div v-for="item in items" :key="item.label" class="menu-item">
      <template v-if="isVisible(item)">
        <router-link
          v-if="item.url"
          :to="item.url"
          class="menu-link"
          @click.native="click()"
          >{{ item.label }}
        </router-link>
        <span v-else class="menu-link" @click="item.action()">{{
          item.label
        }}</span>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { LOGOUT } from "@/types";

@Component({
  components: {}
})
export default class TheMenu extends Vue {
  public items: MenuItem[] = [
    { label: "scanner", url: "/scanner", visible: () => this.isVolunteer },
    { label: "home", url: "/#home" },
    { label: "about us", url: "/about" },
    { label: "venue", url: "/venue" },
    { label: "tickets", url: "/tickets" },
    { label: "vote", url: "/v4p" },
    { label: "partners", url: "/partners" },
    // { label: "schedule", url: "/schedule" },
    // { label: "presentations", url: "/presentations" },
    // { label: "speakers", url: "/speakers" },
    // { label: "workshop day", url: "/workshops" },
    { label: "FAQ", url: "/faq" },
    { label: "my profile", url: "/profile", visible: () => this.isLogin },
    { label: "ADMIN", url: "/admin", visible: () => this.isAdmin },
    {
      label: "logout",
      action: () => this.logout(),
      visible: () => this.isLogin
    },
    // { label: "C4P", url: "/c4p", visible: () => !this.isLogin }
    { label: "login", url: "/login", visible: () => !this.isLogin }
  ];

  public isVisible(item: MenuItem): boolean {
    return item.visible === undefined || item.visible();
  }

  public click() {
    this.$emit("linkClicked");
  }

  public get isLogin() {
    return this.$store.getters.isLogin;
  }

  public get isAdmin() {
    return this.$store.getters.isAdmin;
  }

  public get isVolunteer() {
    return this.$store.getters.isVolunteer || this.isAdmin;
  }

  public logout() {
    this.$store.dispatch(LOGOUT);
  }
}

interface MenuItem {
  label: string;
  url?: string;
  action?: () => void;
  visible?: () => boolean;
}
</script>

<style scoped lang="scss">
@import "../assets/colors";
@import "../assets/media";

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
