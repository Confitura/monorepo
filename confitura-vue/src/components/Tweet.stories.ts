import { storiesOf } from "@storybook/vue";

storiesOf("MyButton", module).add(
  "story as a template",
  () => ({
    data() {
      return {
        item: {
          avatar: "https://lorempixel.com/100/100/",
          name: "confitura",
          twitterHandle: "confiturapl",
          time: 1234,
          text: "hello Twitter!"
        }
      };
    },

    template: `<Tweet :model="item"></Tweet>`
  }),
  {
    backgrounds: [
      {
        name: "black",
        value: "#000000",
        default: true
      }
    ]
  }
);
