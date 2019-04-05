<template>
    <article class="tweet">
        <div class="tweet__avatar">
            <img class="tweet__img" :src="model.avatar" alt="twitter_avatar">
        </div>
        <div class="tweet__metadata">
            <span class="tweet__username">{{model.name}}</span>
            <span class="tweet__handle">@{{model.twitterHandle}}</span>
            <span class="tweet__date">{{dateTime}}</span>
        </div>
        <div class="tweet__message" v-html="message"></div>
    </article>
</template>
<script lang="ts">
  import dayjs from 'dayjs';
  import linkifyHtml from 'linkifyjs/html';
  import { Component, Prop, Vue } from 'vue-property-decorator';

  @Component({})
  export default class Tweet extends Vue {
    @Prop()
    public model!: TweetItem;

    get dateTime() {
      return dayjs.unix(this.model.time).format('DD MMM YY, hh:mm');
    }

    get message() {
      return linkifyHtml(this.model.text, {
        defaultProtocol: 'https',
        className: 'tweet__link',
      });
    }
  }

  export interface TweetItem {
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
    @import '../assets/media';

    .tweet {
        display: grid;
        grid-template-columns: auto 1fr;
        grid-column-gap: 1rem;
        grid-row-gap: 1rem;
        font-size: 1rem;
        color: #ffffff;
        @include md() {
            grid-column-gap: 2rem;
            grid-row-gap: 2rem;
            &__avatar {
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
            align-content: center;
            flex-wrap: wrap;

        }

        &__message {
            grid-column: span 2;
            @include md() {
                grid-column: span 1;
            }
        }

        &__username {
            color: $brand;
        }

        &__handle {
            padding-left: 0.5rem;
            padding-right: 0.5rem;
        }

    }
</style>
<style lang="scss">
    @import '../assets/colors';

    .tweet__link {
        color: $brand;
        text-decoration: none;
    }
</style>
