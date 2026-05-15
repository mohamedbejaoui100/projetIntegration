<template>
  <transition name="modal">
    <div class="modal-overlay" v-if="show" @click.self="$emit('close')">
      <div class="creator-box">

        <!-- Header -->
        <div class="creator-header">
          <div class="creator-header-left">
            <div class="creator-icon">📚</div>
            <div>
              <h2 class="creator-title">{{ mode === 'create' ? 'Créer un nouveau cours' : 'Modifier le cours' }}</h2>
              <p class="creator-sub">Remplissez les informations et ajoutez un modèle 3D obligatoire</p>
            </div>
          </div>
          <button class="close-btn" @click="$emit('close')">✕</button>
        </div>

        <!-- Body — 2 colonnes -->
        <div class="creator-body">

          <!-- Colonne gauche — Formulaire -->
          <div class="form-col">

            <div class="form-group">
              <label>Titre du cours <span class="required">*</span></label>
              <div :class="['input-wrap', { focused: focus.title }]">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><path d="M4 6h16M4 12h16M4 18h7"/></svg>
                <input
                  v-model="form.title"
                  placeholder="Ex: Introduction à la modélisation 3D"
                  @focus="focus.title = true"
                  @blur="focus.title = false"
                  class="f-input"
                />
              </div>
            </div>

            <div class="form-group">
              <label>Description</label>
              <textarea
                v-model="form.description"
                placeholder="Décrivez le contenu et les objectifs du cours..."
                class="f-textarea"
                rows="4"
              ></textarea>
            </div>

            <!-- Modèle 3D section -->
            <div class="model-section">
              <div class="model-section-header">
                <span class="model-section-title">🧊 Modèle 3D principal</span>
                <span class="model-section-hint required-badge">obligatoire</span>
              </div>

              <!-- Mode switcher -->
              <div class="mode-switch">
                <button
                  :class="['mode-btn', { active: inputMode === 'url' }]"
                  @click="inputMode = 'url'">
                  🔗 URL
                </button>
                <button
                  :class="['mode-btn', { active: inputMode === 'upload' }]"
                  @click="inputMode = 'upload'">
                  📁 Fichier local
                </button>
              </div>

              <!-- URL input -->
              <div v-if="inputMode === 'url'" class="form-group" style="margin-bottom:0">
                <div :class="['input-wrap', { focused: focus.url }]">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
                  <input
                    v-model="form.object3dUrl"
                    placeholder="https://exemple.com/model.glb"
                    @focus="focus.url = true"
                    @blur="focus.url = false"
                    @keyup.enter="tryLoadModel()"
                    class="f-input"
                  />
                  <button class="load-btn" @click="tryLoadModel()" :disabled="!form.object3dUrl">
                    Charger
                  </button>
                </div>
                <p class="url-hint">Formats supportés : .glb, .gltf — Appuyez Entrée ou cliquez Charger</p>
              </div>

              <!-- File upload -->
              <div v-else class="upload-zone"
                @click="triggerFileInput"
                @dragover.prevent
                @drop.prevent="handleFileDrop">
                <input
                  ref="fileInput"
                  type="file"
                  accept=".glb,.gltf"
                  style="display:none"
                  @change="handleFileChange"
                />
                <div v-if="!localFileName" class="upload-placeholder">
                  <div class="upload-icon-3d">🧊</div>
                  <p class="upload-text">Cliquez ou glissez un fichier .glb / .gltf</p>
                  <p class="upload-hint">Fichier local — sera chargé directement dans le viewer</p>
                </div>
                <div v-else class="upload-loaded">
                  <span class="upload-file-name">✅ {{ localFileName }}</span>
                  <button class="clear-file" @click.stop="clearFile">✕</button>
                </div>
              </div>

              <!-- Quick models -->
              <div class="quick-models">
                <p class="quick-label">Modèles de démonstration :</p>
                <div class="quick-list">
                  <button
                    v-for="m in demoModels" :key="m.name"
                    class="quick-btn"
                    :class="{ active: form.object3dUrl === m.url }"
                    @click="loadDemoModel(m)">
                    {{ m.icon }} {{ m.name }}
                  </button>
                </div>
              </div>

              <!-- Warning si pas de modèle -->
              <div class="model-warning" v-if="!activeModelUrl && showWarning">
                ⚠️ Veuillez charger un modèle 3D pour continuer
              </div>
            </div>
          </div>

          <!-- Colonne droite — Viewer 3D -->
          <div class="viewer-col">
            <div class="viewer-label">
              <span>Aperçu 3D</span>
              <div class="viewer-status">
                <span v-if="modelActive" class="status-dot active"></span>
                <span v-else class="status-dot"></span>
                <span class="status-text">{{ modelActive ? 'Modèle chargé ✅' : 'Aucun modèle' }}</span>
              </div>
            </div>

            <div class="viewer-wrap">
              <ThreeViewer
                :modelUrl="activeModelUrl"
                :autoRotate="true"
                ref="viewerRef"
              />
              <div class="viewer-empty" v-if="!activeModelUrl">
                <div class="empty-3d-icon">
                  <svg viewBox="0 0 80 80" width="80" height="80">
                    <polygon points="40,8 72,60 8,60" fill="none" stroke="rgba(167,139,250,0.3)" stroke-width="2"/>
                    <polygon points="40,20 62,55 18,55" fill="rgba(167,139,250,0.05)" stroke="rgba(167,139,250,0.15)" stroke-width="1.5"/>
                    <circle cx="40" cy="40" r="5" fill="rgba(167,139,250,0.2)" stroke="rgba(167,139,250,0.5)" stroke-width="1.5"/>
                    <line x1="40" y1="8" x2="40" y2="40" stroke="rgba(167,139,250,0.2)" stroke-width="1"/>
                    <line x1="72" y1="60" x2="40" y2="40" stroke="rgba(167,139,250,0.2)" stroke-width="1"/>
                    <line x1="8" y1="60" x2="40" y2="40" stroke="rgba(167,139,250,0.2)" stroke-width="1"/>
                  </svg>
                </div>
                <p class="empty-3d-text">Entrez une URL ou uploadez un fichier<br/>pour voir l'aperçu 3D</p>
              </div>
            </div>

            <!-- Viewer controls info -->
            <div class="viewer-controls-info" v-if="activeModelUrl">
              <span>🖱️ Clic + glisser</span>
              <span>🔍 Scroll pour zoomer</span>
              <span>✋ Clic droit pour déplacer</span>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="creator-footer">
          <div class="footer-left">
            <div class="form-progress">
              <div class="progress-item" :class="{ done: form.title }">
                {{ form.title ? '✅' : '⭕' }} Titre
              </div>
              <div class="progress-item" :class="{ done: form.description }">
                {{ form.description ? '✅' : '⭕' }} Description
              </div>
              <div class="progress-item" :class="{ done: activeModelUrl }">
                {{ activeModelUrl ? '✅' : '⭕' }} Modèle 3D <span style="color:#f87171;font-size:0.65rem">*</span>
              </div>
            </div>
          </div>
          <div class="footer-right">
            <button class="btn-cancel" @click="$emit('close')">Annuler</button>
            <button
              class="btn-create"
              @click="handleSubmit"
              :disabled="!form.title || !activeModelUrl || isLoading">
              <span v-if="!isLoading">
                {{ mode === 'create' ? '🚀 Créer le cours + Slide 3D' : '💾 Sauvegarder' }}
              </span>
              <div v-else class="spin"></div>
            </button>
          </div>
        </div>

      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import ThreeViewer from './ThreeViewer.vue'

const props = defineProps({
  show: { type: Boolean, default: false },
  mode: { type: String, default: 'create' },
  initialData: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['close', 'submit'])

const isLoading = ref(false)
const inputMode = ref('url')
const localFileName = ref('')
const localFileUrl = ref('')
const fileInput = ref(null)
const viewerRef = ref(null)
const showWarning = ref(false)

const focus = ref({ title: false, url: false })

const form = ref({
  title: '',
  description: '',
  object3dUrl: ''
})

const activeModelUrl = computed(() => {
  if (inputMode.value === 'upload') return localFileUrl.value
  return form.value.object3dUrl || ''
})

const modelActive = computed(() => !!activeModelUrl.value)

const demoModels = [
  {
    name: 'Canard',
    icon: '🦆',
    url: 'https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Binary/Duck.glb'
  },
  {
    name: 'Casque',
    icon: '⛑️',
    url: 'https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/DamagedHelmet/glTF-Binary/DamagedHelmet.glb'
  },
  {
    name: 'Bouteille',
    icon: '🍶',
    url: 'https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/WaterBottle/glTF-Binary/WaterBottle.glb'
  },
  { name: 'Flamant',   icon: '🦩', url: 'https://threejs.org/examples/models/gltf/Flamingo.glb' },
    { name: 'Cheval',    icon: '🐴', url: 'https://threejs.org/examples/models/gltf/Horse.glb' },
    { name: 'Astronaute',icon: '🧑‍🚀', url: 'https://threejs.org/examples/models/gltf/RobotExpressive/RobotExpressive.glb' },
  {
    name: 'Boîte',
    icon: '📦',
    url: 'https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Box/glTF-Binary/Box.glb'
  },
]

// Watch initialData pour le mode edit
watch(() => props.initialData, (data) => {
  if (data && props.mode === 'edit') {
    form.value.title = data.title || ''
    form.value.description = data.description || ''
    form.value.object3dUrl = data.object3dUrl || ''
  }
}, { immediate: true })

watch(() => props.show, (val) => {
  if (val && props.mode === 'create') {
    form.value = { title: '', description: '', object3dUrl: '' }
    localFileName.value = ''
    localFileUrl.value = ''
    inputMode.value = 'url'
    showWarning.value = false
  }
})

function tryLoadModel() {
  // ThreeViewer watcher gère le rechargement automatique
}

function loadDemoModel(m) {
  inputMode.value = 'url'
  form.value.object3dUrl = m.url
}

function triggerFileInput() {
  fileInput.value?.click()
}

function handleFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  processFile(file)
}

function handleFileDrop(e) {
  const file = e.dataTransfer.files[0]
  if (!file) return
  if (!file.name.match(/\.(glb|gltf)$/i)) {
    alert('Format non supporté — utilisez .glb ou .gltf')
    return
  }
  processFile(file)
}

function processFile(file) {
  localFileName.value = file.name
  const url = URL.createObjectURL(file)
  localFileUrl.value = url
  form.value.object3dUrl = url
}

function clearFile() {
  localFileName.value = ''
  localFileUrl.value = ''
  form.value.object3dUrl = ''
  if (fileInput.value) fileInput.value.value = ''
}

function handleSubmit() {
  if (!form.value.title) return
  if (!activeModelUrl.value) {
    showWarning.value = true
    return
  }
  showWarning.value = false
  isLoading.value = true
  emit('submit', {
    title: form.value.title,
    description: form.value.description,
    object3dUrl: activeModelUrl.value
  })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap');
* { box-sizing: border-box; margin: 0; padding: 0; }

.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(6,9,26,0.92);
  backdrop-filter: blur(12px);
  display: flex; align-items: center; justify-content: center;
  z-index: 100; padding: 1rem;
}

.creator-box {
  background: #0e1228;
  border: 1px solid rgba(167,139,250,0.15);
  border-radius: 22px; width: 100%; max-width: 1100px; max-height: 95vh;
  display: flex; flex-direction: column;
  animation: popIn 0.35s cubic-bezier(0.34,1.56,0.64,1);
  overflow: hidden;
  box-shadow: 0 40px 80px rgba(0,0,0,0.5), 0 0 0 1px rgba(167,139,250,0.08);
}
@keyframes popIn { from { opacity:0; transform:scale(0.9) translateY(20px); } to { opacity:1; transform:none; } }

.creator-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.3rem 1.8rem;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  background: rgba(167,139,250,0.04);
}
.creator-header-left { display: flex; align-items: center; gap: 12px; }
.creator-icon { font-size: 1.8rem; }
.creator-title { font-family: 'Syne', sans-serif; font-size: 1.1rem; font-weight: 700; color: #fff; margin-bottom: 2px; }
.creator-sub { font-size: 0.78rem; color: rgba(255,255,255,0.35); }
.close-btn {
  background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.08);
  border-radius: 50%; width: 30px; height: 30px;
  display: flex; align-items: center; justify-content: center;
  color: rgba(255,255,255,0.4); cursor: pointer; font-size: 0.85rem; transition: all 0.2s;
}
.close-btn:hover { background: rgba(255,255,255,0.1); color: #fff; }

.creator-body {
  display: grid; grid-template-columns: 420px 1fr;
  gap: 0; flex: 1; overflow: hidden; min-height: 500px;
}

.form-col {
  padding: 1.5rem 1.8rem; overflow-y: auto;
  border-right: 1px solid rgba(255,255,255,0.06);
  display: flex; flex-direction: column; gap: 1rem;
}

.form-group { display: flex; flex-direction: column; gap: 5px; }
.form-group label { font-size: 0.78rem; font-weight: 500; color: rgba(255,255,255,0.5); letter-spacing: 0.02em; }
.required { color: #f87171; }

.input-wrap {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.09);
  border-radius: 10px; padding: 0.65rem 0.9rem; transition: border-color 0.2s, box-shadow 0.2s;
}
.input-wrap.focused { border-color: rgba(167,139,250,0.45); box-shadow: 0 0 0 3px rgba(167,139,250,0.08); }
.input-wrap svg { color: rgba(255,255,255,0.28); flex-shrink: 0; }
.f-input { background: none; border: none; outline: none; color: #fff; font-family: 'DM Sans', sans-serif; font-size: 0.88rem; flex: 1; }
.f-input::placeholder { color: rgba(255,255,255,0.2); }

.f-textarea {
  width: 100%; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.09);
  border-radius: 10px; padding: 0.65rem 0.9rem; color: #fff;
  font-family: 'DM Sans', sans-serif; font-size: 0.88rem; outline: none; resize: vertical; transition: border-color 0.2s;
}
.f-textarea:focus { border-color: rgba(167,139,250,0.45); }
.f-textarea::placeholder { color: rgba(255,255,255,0.2); }

.model-section {
  background: rgba(167,139,250,0.04); border: 1px solid rgba(167,139,250,0.1);
  border-radius: 14px; padding: 1rem; display: flex; flex-direction: column; gap: 0.8rem;
}
.model-section-header { display: flex; align-items: center; justify-content: space-between; }
.model-section-title { font-size: 0.85rem; font-weight: 600; color: rgba(255,255,255,0.75); }
.model-section-hint { font-size: 0.7rem; color: rgba(255,255,255,0.25); background: rgba(255,255,255,0.05); padding: 2px 8px; border-radius: 10px; }
.required-badge { background: rgba(239,68,68,0.1) !important; color: #f87171 !important; border: 1px solid rgba(239,68,68,0.2); }

.mode-switch { display: flex; gap: 4px; background: rgba(255,255,255,0.04); border-radius: 9px; padding: 3px; }
.mode-btn { flex: 1; padding: 0.45rem; background: transparent; border: none; border-radius: 7px; color: rgba(255,255,255,0.4); font-family: 'DM Sans', sans-serif; font-size: 0.8rem; cursor: pointer; transition: all 0.2s; }
.mode-btn.active { background: rgba(167,139,250,0.15); color: #c4b5fd; }

.load-btn { padding: 4px 12px; background: rgba(167,139,250,0.15); border: 1px solid rgba(167,139,250,0.25); border-radius: 7px; color: #a78bfa; font-size: 0.75rem; cursor: pointer; transition: all 0.2s; white-space: nowrap; }
.load-btn:hover { background: rgba(167,139,250,0.25); }
.load-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.url-hint { font-size: 0.72rem; color: rgba(255,255,255,0.25); margin-top: 4px; }

.upload-zone { border: 2px dashed rgba(167,139,250,0.2); border-radius: 10px; cursor: pointer; padding: 1.2rem; text-align: center; transition: all 0.2s; }
.upload-zone:hover { border-color: rgba(167,139,250,0.4); background: rgba(167,139,250,0.04); }
.upload-icon-3d { font-size: 2rem; margin-bottom: 6px; }
.upload-text { font-size: 0.83rem; color: rgba(255,255,255,0.55); margin-bottom: 3px; }
.upload-hint { font-size: 0.72rem; color: rgba(255,255,255,0.25); }
.upload-loaded { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.upload-file-name { font-size: 0.83rem; color: #a78bfa; }
.clear-file { background: rgba(239,68,68,0.1); border: none; border-radius: 5px; color: #f87171; cursor: pointer; padding: 2px 7px; }

.quick-models { display: flex; flex-direction: column; gap: 6px; }
.quick-label { font-size: 0.72rem; color: rgba(255,255,255,0.3); }
.quick-list { display: flex; flex-wrap: wrap; gap: 6px; }
.quick-btn { padding: 5px 12px; font-size: 0.78rem; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 20px; color: rgba(255,255,255,0.5); cursor: pointer; transition: all 0.2s; }
.quick-btn:hover { border-color: rgba(167,139,250,0.3); color: #c4b5fd; }
.quick-btn.active { border-color: #a78bfa; background: rgba(167,139,250,0.12); color: #a78bfa; }

.model-warning { background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.2); border-radius: 8px; padding: 8px 12px; font-size: 0.78rem; color: #f87171; text-align: center; }

.viewer-col { padding: 1.5rem 1.8rem; display: flex; flex-direction: column; gap: 0.8rem; background: rgba(6,9,26,0.4); position: relative; }
.viewer-label { display: flex; align-items: center; justify-content: space-between; font-family: 'Syne', sans-serif; font-size: 0.85rem; font-weight: 600; color: rgba(255,255,255,0.6); }
.viewer-status { display: flex; align-items: center; gap: 6px; }
.status-dot { width: 7px; height: 7px; border-radius: 50%; background: rgba(255,255,255,0.15); }
.status-dot.active { background: #34d399; box-shadow: 0 0 6px rgba(52,211,153,0.5); animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{opacity:1;} 50%{opacity:0.5;} }
.status-text { font-size: 0.72rem; color: rgba(255,255,255,0.35); font-family: 'DM Sans', sans-serif; }

.viewer-wrap { flex: 1; border-radius: 14px; overflow: hidden; border: 1px solid rgba(167,139,250,0.1); position: relative; min-height: 420px; background: #060918; }
.viewer-empty { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 14px; }
.empty-3d-icon { opacity: 0.6; animation: float 4s ease-in-out infinite; }
@keyframes float { 0%,100%{transform:translateY(0);} 50%{transform:translateY(-8px);} }
.empty-3d-text { font-size: 0.78rem; color: rgba(255,255,255,0.25); text-align: center; line-height: 1.6; }
.viewer-controls-info { display: flex; gap: 1rem; justify-content: center; font-size: 0.72rem; color: rgba(255,255,255,0.25); }

.creator-footer { display: flex; align-items: center; justify-content: space-between; padding: 1rem 1.8rem; border-top: 1px solid rgba(255,255,255,0.06); background: rgba(6,9,26,0.5); }
.footer-left { flex: 1; }
.form-progress { display: flex; gap: 1rem; }
.progress-item { font-size: 0.75rem; color: rgba(255,255,255,0.3); transition: color 0.3s; }
.progress-item.done { color: #34d399; }
.footer-right { display: flex; gap: 8px; }

.btn-cancel { padding: 0.6rem 1.1rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.09); border-radius: 10px; color: rgba(255,255,255,0.5); font-family: 'DM Sans', sans-serif; font-size: 0.85rem; cursor: pointer; transition: all 0.2s; }
.btn-cancel:hover { background: rgba(255,255,255,0.08); color: #fff; }

.btn-create { padding: 0.6rem 1.5rem; background: linear-gradient(135deg, #a78bfa, #7c3aed); border: none; border-radius: 10px; color: #fff; font-family: 'Syne', sans-serif; font-size: 0.88rem; font-weight: 700; cursor: pointer; transition: all 0.2s; min-width: 180px; display: flex; align-items: center; justify-content: center; }
.btn-create:hover:not(:disabled) { box-shadow: 0 6px 20px rgba(167,139,250,0.35); transform: translateY(-1px); }
.btn-create:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }

.spin { width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.25); border-top-color: #fff; border-radius: 50%; animation: spin 0.7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.modal-enter-active,.modal-leave-active { transition: all 0.3s ease; }
.modal-enter-from,.modal-leave-to { opacity:0; }

@media (max-width: 768px) {
  .creator-body { grid-template-columns: 1fr; }
  .viewer-col { min-height: 300px; border-right: none; border-top: 1px solid rgba(255,255,255,0.06); }
}
</style>