<template>
  <canvas class="stars" ref="canvas"></canvas>
</template>
<script setup lang="ts">

let animationId: number | null = null

onMounted(() => {
  generateStars()
})

onBeforeUnmount(() => {
  if (animationId !== null) {
    clearInterval(animationId)
  }
})


function generateStars() {
  const height: number = window.outerHeight
  const width: number =
    window.outerWidth < 1200 ? window.outerWidth * 2 : window.outerWidth
  const context = getContext(width, height)
  const stars: Star[] = createStars(width, height)
  animationId = setInterval(animate, 2000 / 24)

  function animate() {
    if (context !== null) {
      context.clearRect(0, 0, width, height)
      stars.forEach(star => star.draw(context))
    }
  }
}

const canvas = useTemplateRef<HTMLCanvasElement>('canvas')

function getContext(width: number, height: number) {
  canvas.value.width = width
  canvas.value.height = height
  return canvas.value.getContext('2d')
}

function createStars(width: number, height: number) {
  return Array.from(Array(1200), () => {
    const x = Math.round(Math.random() * width)
    const y = Math.round(Math.random() * height)
    const length = 1 + Math.random() * 1.5
    const opacity = Math.random()
    return new Star(x, y, length, opacity)
  })
}

class Star {
  private direction: number = 1
  private increment = Math.random() * 0.03
  private maxOpacity = 0.7

  constructor(
    private x: number,
    private y: number,
    private length: number,
    private opacity: number
  ) {
    this.opacity = Math.max(opacity, this.maxOpacity)
  }

  draw(context: CanvasRenderingContext2D) {
    this.changeOpacity(context)
    this.doDraw(context)
  }

  private doDraw(context: CanvasRenderingContext2D) {
    context.rotate(Math.PI / 10)
    context.save()
    context.translate(this.x, this.y)
    context.beginPath()
    context.arc(0, 0, this.length, 0, 2 * Math.PI)
    context.fillStyle = `rgba(255, 255, 255, ${this.opacity})`
    context.shadowBlur = 5
    context.shadowColor = '#ffffff'
    context.fill()
    context.restore()
  }

  private changeOpacity(context: CanvasRenderingContext2D) {
    if (this.opacity > this.maxOpacity) {
      this.direction = -1
    } else if (this.opacity <= 0) {
      this.direction = 1
      this.x = Math.round(Math.random() * context.canvas.width)
      this.y = Math.round(Math.random() * context.canvas.height)
    }

    this.opacity += this.increment * this.direction
  }
}
</script>
<style scoped lang="scss">
@use "~/assets/fonts" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;
@use "~/assets/colors" as *;

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
