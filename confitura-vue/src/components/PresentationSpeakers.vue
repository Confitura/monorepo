<template>
    <div class="speakers">
        <div v-for="speaker in speakers"
             :key="speaker.id"
             class="speaker"
             @click="show(speaker)">
            <LazyImage
                    :src="speaker.photo"
                    class="speaker__photo"/>
            <div class="speaker__name">
                <span>{{speaker.name | firstName}}</span>
                <span>{{speaker.name | lastName}}</span>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import LazyImage from './LazyImage.vue';
  import { UserProfile } from '@/types';

  @Component({
    components: { LazyImage },
  })
  export default class PresentationSpeakers extends Vue {
    @Prop({ required: true })
    public speakers!: UserProfile[];

    public show({ id }: UserProfile) {
      if (id) {
        this.$router.push({ name: 'speaker', params: { id } });
      }
    }
  }
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .speakers {
        display: flex;
        flex-direction: column;
        @include md() {
            flex-direction: row;
        }
    }

    .speaker {
        display: flex;
        justify-items: center;
        align-items: center;
        margin-right: 3rem;
        margin-bottom: 2rem;
        cursor: pointer;

        &__photo {
            flex-grow: 0;
            width: 45px;
            height: 45px;
            object-fit: cover;
            margin-right: 1rem;
            background-color: #000000;
        }

        &__name {
            display: flex;
            flex-direction: column;
            font-size: 1rem;
            font-weight: bold;
        }
    }
</style>
