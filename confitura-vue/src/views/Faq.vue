<template>
    <div class="faq">
        <PageHeader/>
        <Box class="content" color="white">
            <p class="foreword">Everything has started in 2007, when the group of passionate Java developers decided to
                create a programming event for the community. Today we're running the conference for more than 1800
                attendees. 35 talks in 5 parallel sessions give an opportunity to learn new thin</p>
            <section class="questions">
                <h2>Registration</h2>
                <h3>Question 1</h3>
                <div>Answer 1</div>
                <h3>Question 2</h3>
                <div>Answer 2</div>
                <h3>Question 3</h3>
                <div>Answer 3</div>

                <h2>Call 4 Papers</h2>
                <h3>Question 1</h3>
                <div>Answer 1</div>
                <h3>Question 2</h3>
                <div>Answer 2</div>
                <h3>Question 3</h3>
                <div>Answer 3</div>

            </section>
        </Box>
    </div>

</template>

<script lang="ts">
    import { Component, Vue } from "vue-property-decorator";
    import Box from "@/components/Box.vue";
    import PageHeader from '@/views/PageHeader.vue';

    @Component({
        components: {PageHeader, Box},
    })
    export default class Faq extends Vue {

        mounted() {


        }
    }
</script>

<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/fonts";

    .content {

        .foreword {
            text-align: left;
            color: $brand;
            font-size: 1.5rem;
            width: 50%;
        }

        .questions {
            display: grid;
            grid-template-columns: 1fr 1fr;
            text-align: left;

            h2 {
                font-size: 2.5rem;
                grid-column: 1 / 3;
                color: $brand;
            }

            h3 {
                font-size: 1.5rem;
                grid-column: 2 / 3;
            }

            div {
                grid-column: 2 /3;
            }
        }
    }
</style>
