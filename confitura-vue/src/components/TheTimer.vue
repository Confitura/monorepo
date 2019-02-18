<template>
    <div class="timer" :key="seconds">
        <div class="timer-part">
            <span class="timer-value">{{days  | pad}}</span>
            <span class="timer-unit">days</span>
        </div>
        <span class="timer-part-separator"></span>
        <div class="timer-part">
            <span class="timer-value">{{hours  | pad}}</span>
            <span class="timer-unit">hours</span>
        </div>
        <span class="timer-part-separator"></span>
        <div class="timer-part">
            <span class="timer-value">{{minutes  | pad}}</span>
            <span class="timer-unit">minutes</span>
        </div>
        <span class="timer-part-separator"></span>
        <div class="timer-part">
            <span class="timer-value">{{seconds | pad}}</span>
            <span class="timer-unit">seconds</span>
        </div>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import dayjs from 'dayjs';

@Component({
    filters: {
        pad(value: number) {
            return `${value}`.length === 1 ? `0${value}` : value;
        },
    },
})
export default class TheTimer extends Vue {
    public start: dayjs.Dayjs;
    public now: dayjs.Dayjs = dayjs();
    public days = 0;
    public hours = 0;
    public minutes = 0;
    public seconds = 0;
    private handle: number = 0;

    public mounted() {
        this.start = dayjs(this.$store.state.date);
        this.recalculate();
        this.handle = setInterval(() => {
            this.recalculate();
        }, 1000);
    }

    public beforeDestroy() {
        clearInterval(this.handle);
    }

    private recalculate() {
        this.now = dayjs();
        this.days = this.start.diff(this.now, 'day');
        this.hours = this.start
            .subtract(this.days, 'day')
            .diff(this.now, 'hour');
        this.minutes = this.start
            .subtract(this.days, 'day')
            .subtract(this.hours, 'hour')
            .diff(this.now, 'minute');
        this.seconds = this.start
            .subtract(this.days, 'day')
            .subtract(this.hours, 'hour')
            .subtract(this.minutes, 'minute')
            .diff(this.now, 'second');
    }

}
</script>

<style scoped lang="scss">
    @import "../assets/media";
    @import "../assets/fonts";
    @import "../assets/colors";
    .timer {
        color: $brand;
        display: flex;
        align-self: center;
    }

    .timer-part {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .timer-value {
        font-family: $font-bold;
        font-size: 3rem;
    }

    .timer-part-separator:after {
        content: ':';
        font-size: 3rem;
        font-weight: bold;
        margin-left: 10px;
        margin-right: 10px;
    }

    .timer-unit {

    }
</style>