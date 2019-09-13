<template>
  <canvas class="stars" ref="canvas"></canvas>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component({})
export default class HomeMainBannerStars extends Vue {
  private animationId: number | null = null;

  protected mounted() {
    this.generateStars();
  }

  private generateStars() {
    const height: number = window.outerHeight;
    const width: number =
      window.outerWidth < 1200 ? window.outerWidth * 2 : window.outerWidth;
    const context = this.getContext(width, height);
    const stars: Star[] = this.createStars(width, height);
    this.animationId = setInterval(animate, 2000 / 24);

    function animate() {
      if (context !== null) {
        context.clearRect(0, 0, width, height);
        stars.forEach(star => star.draw(context));
      }
    }
  }

  protected beforeDestroy() {
    if (this.animationId !== null) {
      clearInterval(this.animationId);
    }
  }

  private getContext(width: number, height: number) {
    const canvas = this.$refs.canvas as HTMLCanvasElement;
    canvas.width = width;
    canvas.height = height;
    return canvas.getContext("2d");
  }

  private createStars(width: number, height: number) {
    return Array.from(Array(1200), () => {
      const x = Math.round(Math.random() * width);
      const y = Math.round(Math.random() * height);
      const length = 1 + Math.random() * 1.5;
      const opacity = Math.random();
      return new Star(x, y, length, opacity);
    });
  }
}

class Star {
  private direction: number = 1;
  private increment = Math.random() * 0.03;
  private maxOpacity = 0.7;
  constructor(
    private x: number,
    private y: number,
    private length: number,
    private opacity: number
  ) {
    this.opacity = Math.max(opacity, this.maxOpacity);
  }

  draw(context: CanvasRenderingContext2D) {
    this.changeOpacity(context);
    this.doDraw(context);
  }

  private doDraw(context: CanvasRenderingContext2D) {
    context.rotate(Math.PI / 10);
    context.save();
    context.translate(this.x, this.y);
    context.beginPath();
    context.arc(0, 0, this.length, 0, 2 * Math.PI);
    context.fillStyle = `rgba(255, 255, 255, ${this.opacity})`;
    context.shadowBlur = 5;
    context.shadowColor = "#ffffff";
    context.fill();
    context.restore();
  }

  private changeOpacity(context: CanvasRenderingContext2D) {
    if (this.opacity > this.maxOpacity) {
      this.direction = -1;
    } else if (this.opacity <= 0) {
      this.direction = 1;
      this.x = Math.round(Math.random() * context.canvas.width);
      this.y = Math.round(Math.random() * context.canvas.height);
    }

    this.opacity += this.increment * this.direction;
  }
}
</script>
<style scoped lang="scss">
@import "../assets/fonts";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/colors";

.stars {
  z-index: 1;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  display: block;
  background-color: black;
  object-fit: cover;
}
</style>
