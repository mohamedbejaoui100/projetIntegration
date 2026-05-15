<template>
  <div class="three-container">
    <canvas ref="canvasRef"></canvas>
    <div class="controls-hint" v-if="modelLoaded">
      🖱️ Clic + glisser pour tourner · Scroll pour zoomer
    </div>
    <div class="loading-3d" v-if="isLoading">
      <div class="spinner-3d"></div>
      <span>Chargement du modèle 3D...</span>
    </div>
    <div class="error-3d" v-if="hasError">
      ❌ Impossible de charger le modèle
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as THREE from 'three'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'

const props = defineProps({
  modelUrl: { type: String, default: '' },
  autoRotate: { type: Boolean, default: true }
})

const canvasRef = ref(null)
const modelLoaded = ref(false)
const isLoading = ref(false)
const hasError = ref(false)

let scene, camera, renderer, controls, animId, currentModel

function init() {
  const canvas = canvasRef.value
  const W = canvas.clientWidth
  const H = canvas.clientHeight

  // Scene
  scene = new THREE.Scene()
  // Scene background — plus sombre et professionnel
  scene.background = new THREE.Color(0x060918)

  // Camera
  camera = new THREE.PerspectiveCamera(45, W / H, 0.1, 100)
  camera.position.set(0, 1.5, 4)

  // Renderer
  renderer = new THREE.WebGLRenderer({ canvas, antialias: true })
  renderer.setSize(W, H)
  renderer.setPixelRatio(window.devicePixelRatio)
  renderer.shadowMap.enabled = true

  // Lights
  const ambient = new THREE.AmbientLight(0xffffff, 0.6)
  scene.add(ambient)
  const dirLight = new THREE.DirectionalLight(0x00f5d4, 1.2)
  dirLight.position.set(5, 10, 5)
  dirLight.castShadow = true
  scene.add(dirLight)
  const fillLight = new THREE.DirectionalLight(0x6366f1, 0.5)
  fillLight.position.set(-5, 0, -5)
  scene.add(fillLight)

  // Grid
  // Supprimer l'ancien grid et remplacer par une grille plus grande
  const grid = new THREE.GridHelper(30, 40, 0x1a2a4a, 0x0d1a2e)
  grid.position.y = -1.5
  scene.add(grid)



  // Controls
  controls = new OrbitControls(camera, canvas)
  controls.enableDamping = true
  controls.dampingFactor = 0.05
  controls.autoRotate = props.autoRotate
  controls.autoRotateSpeed = 1.5

  // Load model
  if (props.modelUrl) loadModel(props.modelUrl)

  // Animate
  animate()

  // Resize
  window.addEventListener('resize', onResize)
}

function loadModel(url) {
  if (!url) return
  isLoading.value = true
  hasError.value = false
  modelLoaded.value = false

  if (currentModel) {
    scene.remove(currentModel)
    currentModel = null
  }

  const loader = new GLTFLoader()
  loader.load(
    url,
    (gltf) => {
      currentModel = gltf.scene

      // Auto-center le modèle
      const box = new THREE.Box3().setFromObject(currentModel)
      const center = box.getCenter(new THREE.Vector3())
      const size = box.getSize(new THREE.Vector3())
      const maxDim = Math.max(size.x, size.y, size.z)
      const scale = 2 / maxDim
      currentModel.scale.setScalar(scale)
      currentModel.position.sub(center.multiplyScalar(scale))

      scene.add(currentModel)
      isLoading.value = false
      modelLoaded.value = true
    },
    (progress) => {
      console.log('Loading:', (progress.loaded / progress.total * 100).toFixed(0) + '%')
    },
    (error) => {
      console.error('Error loading model:', error)
      isLoading.value = false
      hasError.value = true
    }
  )
}

function animate() {
  animId = requestAnimationFrame(animate)
  controls.update()
  renderer.render(scene, camera)
}

function onResize() {
  const canvas = canvasRef.value
  if (!canvas) return
  const W = canvas.clientWidth
  const H = canvas.clientHeight
  camera.aspect = W / H
  camera.updateProjectionMatrix()
  renderer.setSize(W, H)
}

watch(() => props.modelUrl, (newUrl) => {
  if (newUrl) loadModel(newUrl)
})

onMounted(() => init())
onUnmounted(() => {
  cancelAnimationFrame(animId)
  window.removeEventListener('resize', onResize)
  renderer?.dispose()
})
</script>

<style scoped>
.three-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 0;        /* ← changer 400px en 0 */
  border-radius: 0;
  overflow: hidden;
  background: #060918;
}
canvas { width: 100% !important; height: 100% !important; display: block; }
.controls-hint {
  position: absolute; bottom: 12px; left: 50%; transform: translateX(-50%);
  background: rgba(5,11,24,0.7); backdrop-filter: blur(8px);
  padding: 6px 14px; border-radius: 20px;
  font-size: 0.75rem; color: rgba(255,255,255,0.5);
}
.loading-3d {
  position: absolute; inset: 0;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  background: rgba(5,11,24,0.8); gap: 12px;
  font-size: 0.88rem; color: rgba(255,255,255,0.6);
}
.spinner-3d {
  width: 40px; height: 40px;
  border: 3px solid rgba(0,245,212,0.2);
  border-top-color: #00f5d4;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.error-3d {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(5,11,24,0.8);
  color: #f87171; font-size: 0.88rem;
}
</style>