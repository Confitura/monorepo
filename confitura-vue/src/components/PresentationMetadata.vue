<template>
    <div class="presentationMetadata">
        <div class="presentationMetadata__group">
            <i class="presentationMetadata__icon fas fa-flag" title="language"></i>
            <div class="presentationMetadata__language">{{language}}</div>
        </div>
        <div class="presentationMetadata__group">
            <i class="presentationMetadata__icon fas fa-graduation-cap" title="level"></i>
            <div class="presentationMetadata__level">{{presentation.level}}</div>
        </div>
        <div class="presentationMetadata__group" v-if="presentation.workshop">
            <i class="presentationMetadata__icon fas fa-hammer" title="workshop"></i>
            <div class="presentationMetadata__workshop">workshop</div>
        </div>
        <div class="presentationMetadata__group">
            <template v-if="hasTags">
                <i class="presentationMetadata__icon  fas fa-tags" title="tags"></i>
                <div class="presentationMetadata__tags">
                    <span class="presentationMetadata__tag" v-for="tag in presentation.tags">{{tag.name}}</span>
                </div>
            </template>
        </div>

    </div>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { Presentation } from '@/types';

  @Component({})
  export default class PresentationMetadata extends Vue {
    @Prop({ required: true })
    public presentation!: Presentation;

    get hasTags(): boolean | null {
      return this.presentation && this.presentation.tags.length > 0;
    }
    get language(): string {
      let language = 'polish';
      if (this.presentation && this.presentation.language === 'en') {
        language = 'english';
      }
      return language;
    }
  }
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .presentationMetadata {
        font-weight: bold;
        display: flex;
        flex-direction: column;
        flex-wrap: wrap;
        font-size: 1.5rem;
        margin-bottom: 2rem;
        @include md(){
            flex-direction: row;
        }

        &__group {
            display: flex;
            margin-bottom: 0.5rem;
        }

        &__icon {
            margin-right: 1rem;
        }

        &__language, &__level, &__workshop {
            margin-right: 1rem;
        }

        &__tag {
            &:not(:last-child)::after {
                content: ', ';
            }
        }

    }
</style>
