<template>
    <div class="usersGrid">
        <div class="user" v-for="user in users"
             @click="show(user)">
            <img :src="user.photo | crop(300)" alt="" class="user__photo">
            <div class="user__name">
                <span>{{user.name | firstName}}</span>
                <span>{{user.name | lastName}}</span>

            </div>
        </div>
    </div>
</template>
<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { UserProfile } from '@/types';

  @Component({
    components: {},
  })
  export default class UsersGrid extends Vue {

    @Prop({ required: true })
    public users!: UserProfile[];

    public show({ id }: UserProfile) {
      if (id) {
        this.$router.push({ name: 'speaker', params: { id } });
      }
    }

  }
</script>
<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .usersGrid {
        display: flex;
        flex-direction: column;
        justify-content: center;
        @include md() {
            flex-direction: row;
            flex-wrap: wrap;
        }
    }

    .user {
        display: flex;
        cursor: pointer;

        &:hover {
            background-color: $brand;
            color: #ffffff;
            transition: all 0.3s linear;
        }

        @include sm-only() {
            &:nth-child(odd) {
                flex-direction: row-reverse;

                .user__name {
                    align-items: flex-end;
                }


            }
        }
        @include md-to-xl() {
            &:nth-child(4n+3), &:nth-child(4n+4) {
                flex-direction: row-reverse;

                .user__name {
                    align-items: flex-end;
                }
            }
        }
        @include xl() {
            &:nth-child(6n+4), &:nth-child(6n+5), &:nth-child(6n+6) {
                flex-direction: row-reverse;

                .user__name {
                    align-items: flex-end;
                }
            }
        }


    }

    .user__photo {
        width: 50vw;
        height: 50vw;
        object-fit: cover;
        background-color: $brand;
        @include md() {
            width: 200px;
            height: 200px;
        }
    }

    .user__name {
        display: flex;
        flex-direction: column;
        justify-content: center;
        font-family: $font-bold;
        font-size: 1.7rem;
        padding: 1rem;
        box-sizing: border-box;
        @include md() {
            width: 200px;
            height: 200px;
        }
    }


</style>
