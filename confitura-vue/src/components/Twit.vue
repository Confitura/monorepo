<template>
  <article class="twit">
    <div class="twit__avatar">
      <img class="twit__img" :src="model.avatar" alt>
    </div>
    <div class="twit__metadata">
      <span class="twit__username">{{model.name}}</span>
      <span class="twit__handle">@{{model.twitterHandle}}</span>
      <span class="twit__date">{{dateTime}}</span>
    </div>
    <div class="twit__message" v-html="message"></div>
  </article>
</template>
<script lang="ts">
import dayjs from 'dayjs';
import linkifyHtml from 'linkifyjs/html';
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component({})
export default class Twit extends Vue {
  @Prop()
  public model!: TwitItem;
  get dateTime() {
    return dayjs.unix(this.model.time).format('DD MMM YY, hh:mm');
  }

  get message() {
    return linkifyHtml(this.model.text, {
      defaultProtocol: 'https',
      className: 'twit__link',
    });
  }
}

export interface TwitItem {
  name: string;
  twitterHandle: string;
  avatar: string;
  text: string;
  time: number;
}
</script>
<style scoped lang="scss">
@import '../assets/colors';
@import '../assets/fonts';

.twit {
  display: grid;
  grid-template-columns: auto 1fr;
  grid-column-gap: 1rem;
  grid-row-gap: 1rem;
  font-size: 1rem;
  color: #ffffff;
  @media all and (min-width: 1000px) {
    grid-column-gap: 2rem;
    grid-row-gap: 2rem;
  }
  &__avatar {
    @media all and (min-width: 1000px) {
      grid-row: span 2;
    }
  }

  &__img {
    border-radius: 50px;
    width: 50px;
  }

  &__metadata {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  &__message {
    grid-column: span 2;
    @media all and (min-width: 1000px) {
      grid-column: span 1;
    }
  }

  &__username {
    color: $brand;
  }

  &__handle,
  &__date {
    padding-left: 5px;
  }
}
</style>
<style lang="scss">
@import '../assets/colors';
.twit__link {
  color: $brand;
  text-decoration: none;
}
</style>