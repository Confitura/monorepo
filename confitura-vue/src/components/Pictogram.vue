<template>
    <div class="pictogram">
        <div class="pictogram__icon-container">
            <img class="pictogram__icon" :src="src" alt="">
        </div>
        <span class="pictogram__value">{{value}}</span>
        <span class="pictogram__unit">{{unit}}</span>
    </div>
</template>
<script lang="ts">
  import { Component, Prop, Vue } from "vue-property-decorator";

  @Component
  export default class Pictogram extends Vue {
    @Prop(String)
    public src!: string;
    @Prop(String)
    public value!: string;
    @Prop(String)
    public unit!: string;


  }
</script>
<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";

    .pictogram {
        display: flex;
        flex-direction: column;

        &__icon-container {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        &__icon {
            margin-bottom: 31px;
        }

        &__value {
            font-size: 4rem;
            font-family: $font-bold;
        }

        &__unit {
            font-size: 1.125rem;
        }
    }

</style>