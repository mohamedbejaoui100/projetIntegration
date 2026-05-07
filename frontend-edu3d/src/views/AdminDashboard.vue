<template>
  <div class="admin-root">
    <!-- Sidebar -->
    <aside :class="['sidebar', { collapsed: sidebarCollapsed }]">
      <div class="sidebar-header">
        <div class="brand">
          <div class="brand-icon">
            <svg width="22" height="22" viewBox="0 0 38 38" fill="none">
              <polygon points="19,2 36,32 2,32" fill="none" stroke="#00f5d4" stroke-width="2.2"/>
              <circle cx="19" cy="19" r="4" fill="#00f5d4"/>
            </svg>
          </div>
          <span class="brand-text" v-if="!sidebarCollapsed">EDU<em>3D</em></span>
        </div>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path v-if="!sidebarCollapsed" d="M15 18l-6-6 6-6"/>
            <path v-else d="M9 18l6-6-6-6"/>
          </svg>
        </button>
      </div>

      <nav class="sidebar-nav">
        <button v-for="item in navItems" :key="item.id"
          :class="['nav-item', { active: activeView === item.id }]"
          @click="activeView = item.id"
          :title="item.label">
          <span class="nav-icon" v-html="item.icon"></span>
          <span class="nav-label" v-if="!sidebarCollapsed">{{ item.label }}</span>
          <span class="nav-badge" v-if="item.badge && !sidebarCollapsed">{{ item.badge }}</span>
        </button>
      </nav>

      <div class="sidebar-footer" v-if="!sidebarCollapsed">
        <div class="admin-info" @click="activeView = 'profile'" style="cursor:pointer">
          <div class="admin-avatar-wrap">
            <img v-if="currentUser.photoUrl" :src="currentUser.photoUrl" class="admin-avatar-img"/>
            <div v-else class="admin-avatar">{{ adminInitial }}</div>
          </div>
          <div class="admin-details">
            <div class="admin-name">{{ currentUser.nom || adminName }}</div>
            <div class="admin-role">{{ currentUser.role }}</div>
          </div>
        </div>
        <button class="logout-btn" @click="logout" title="Déconnexion">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16,17 21,12 16,7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
      </div>
    </aside>

    <!-- Main -->
    <main class="main">
      <!-- Top bar -->
      <header class="topbar">
        <div class="topbar-left">
          <h1 class="page-title">{{ currentPageTitle }}</h1>
          <div class="breadcrumb">Admin / {{ currentPageTitle }}</div>
        </div>
        <div class="topbar-right">
          <div class="search-box">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            <input v-model="globalSearch" placeholder="Rechercher..." @input="handleSearch"/>
          </div>
          <div class="topbar-time">{{ currentTime }}</div>
        </div>
      </header>

      <!-- OVERVIEW -->
      <transition name="fade" mode="out-in">
        <div v-if="activeView === 'overview'" key="overview" class="view">

          <!-- Stats cards -->
          <div class="stats-grid">
            <div class="stat-card" v-for="(s, i) in statsCards" :key="s.label"
              :style="{ animationDelay: i * 0.08 + 's' }">
              <div class="stat-top">
                <div class="stat-icon" :style="{ background: s.bg }">
                  <span v-html="s.icon"></span>
                </div>
                <div :class="['stat-trend', s.trend > 0 ? 'up' : 'down']">
                  {{ s.trend > 0 ? '↑' : '↓' }} {{ Math.abs(s.trend) }}%
                </div>
              </div>
              <div class="stat-val">{{ s.val }}</div>
              <div class="stat-label">{{ s.label }}</div>
              <div class="stat-bar">
                <div class="stat-bar-fill" :style="{ width: s.pct + '%', background: s.color }"></div>
              </div>
            </div>
          </div>

          <!-- Charts row -->
          <div class="charts-row">
            <!-- Users by role -->
            <div class="chart-card">
              <div class="card-header">
                <h3>Répartition des utilisateurs</h3>
              </div>
              <div class="donut-wrap">
                <svg viewBox="0 0 120 120" class="donut-svg">
                  <circle cx="60" cy="60" r="44" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="16"/>
                  <circle cx="60" cy="60" r="44" fill="none" stroke="#00f5d4" stroke-width="16"
                    :stroke-dasharray="`${studentPct * 2.76} 276`" stroke-dashoffset="69" stroke-linecap="round"/>
                  <circle cx="60" cy="60" r="44" fill="none" stroke="#6366f1" stroke-width="16"
                    :stroke-dasharray="`${teacherPct * 2.76} 276`"
                    :stroke-dashoffset="69 - studentPct * 2.76" stroke-linecap="round"/>
                </svg>
                <div class="donut-center">
                  <span class="donut-total">{{ allUsers.length }}</span>
                  <span class="donut-label">Total</span>
                </div>
              </div>
              <div class="donut-legend">
                <div class="legend-item">
                  <span class="dot" style="background:#00f5d4"></span>
                  <span>Étudiants</span>
                  <strong>{{ students.length }}</strong>
                </div>
                <div class="legend-item">
                  <span class="dot" style="background:#6366f1"></span>
                  <span>Enseignants</span>
                  <strong>{{ teachers.length }}</strong>
                </div>
                <div class="legend-item">
                  <span class="dot" style="background:#f59e0b"></span>
                  <span>Admins</span>
                  <strong>{{ admins.length }}</strong>
                </div>
              </div>
            </div>

            <!-- Recent activity -->
            <div class="chart-card flex-2">
              <div class="card-header">
                <h3>Utilisateurs récents</h3>
                <button class="btn-text" @click="activeView = 'users'">Voir tous →</button>
              </div>
              <div class="recent-list">
                <div class="recent-item" v-for="u in recentUsers" :key="u.id">
                  <div class="user-avatar-sm" :style="{ background: roleColor(u.role) }">
                    {{ u.nom?.charAt(0)?.toUpperCase() }}
                  </div>
                  <div class="recent-info">
                    <div class="recent-name">{{ u.nom }}</div>
                    <div class="recent-email">{{ u.email }}</div>
                  </div>
                  <div :class="['role-badge', u.role?.toLowerCase()]">{{ u.role }}</div>
                </div>
                <div class="empty-state" v-if="!recentUsers.length">
                  <span>Aucun utilisateur trouvé</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- USERS VIEW -->
        <div v-else-if="activeView === 'users'" key="users" class="view">
          <!-- Sub tabs -->
          <div class="sub-tabs">
            <button v-for="t in userTabs" :key="t.id"
              :class="['sub-tab', { active: userTab === t.id }]"
              @click="userTab = t.id">
              <span>{{ t.icon }}</span>
              {{ t.label }}
              <span class="count">{{ t.count }}</span>
            </button>
          </div>


          <!-- Table toolbar -->
          <div class="table-toolbar">
            <div class="search-inline">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
              <input v-model="userSearch" :placeholder="`Rechercher un ${userTab === 'students' ? 'étudiant' : 'enseignant'}...`"/>
            </div>
            <button class="btn-add" @click="openModal('create')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" width="16" height="16"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              Ajouter
            </button>
          </div>

          <!-- Table -->
          <div class="table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Utilisateur</th>
                  <th>Email</th>
                  <th>Rôle</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(user, i) in filteredUsers" :key="user.id"
                  :style="{ animationDelay: i * 0.04 + 's' }" class="table-row">
                  <td>
                    <div class="user-cell">
                      <div class="user-avatar-sm" :style="{ background: roleColor(user.role) }">
                        {{ user.nom?.charAt(0)?.toUpperCase() }}
                      </div>
                      <div>
                        <div class="user-name">{{ user.nom }}</div>
                        <div class="user-id">#{{ user.id }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="email-cell">{{ user.email }}</td>
                  <td><span :class="['role-badge', user.role?.toLowerCase()]">{{ user.role }}</span></td>
                  <td><span class="status-badge active">Actif</span></td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn edit" @click="openModal('edit', user)" title="Modifier">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                      </button>
                      <button class="action-btn view" @click="openProfile(user)" title="Voir profil">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      </button>
                      <button class="action-btn delete" @click="confirmDelete(user)" title="Supprimer">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><polyline points="3,6 5,6 21,6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="!filteredUsers.length">
                  <td colspan="5">
                    <div class="empty-state">Aucun résultat trouvé</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- CLASSROOMS VIEW -->
        <div v-else-if="activeView === 'classrooms'" key="classrooms" class="view">
          <div class="empty-state" style="padding:4rem">
            🏛️ Section Classrooms — à développer
          </div>
        </div>

       <!-- PROFILE VIEW -->
       <div v-else-if="activeView === 'profile'" key="profile" class="view">
         <div class="profile-page">

           <!-- Cover -->
           <div class="profile-cover">
             <div class="profile-avatar-wrap">
               <div class="profile-avatar-ring">
                 <img v-if="profileForm.photoUrl" :src="profileForm.photoUrl" class="profile-avatar-img"/>
                 <div v-else class="profile-avatar-placeholder">{{ adminInitial }}</div>
               </div>
             </div>
           </div>

           <div class="profile-content">

             <!-- Carte info gauche -->
             <div class="profile-left">
               <div class="profile-card">
                 <div class="profile-avatar-display">
                   <img v-if="currentUser.photoUrl" :src="currentUser.photoUrl" class="profile-avatar-img-lg"/>
                   <div v-else class="profile-avatar-placeholder-lg">{{ adminInitial }}</div>
                 </div>
                 <h3 class="profile-name-display">{{ currentUser.nom }}</h3>
                 <p class="profile-email-display">{{ currentUser.email }}</p>
                 <span :class="['role-badge', currentUser.role?.toLowerCase()]">{{ currentUser.role }}</span>
                 <div class="profile-stats-row">
                   <div class="p-stat">
                     <span class="p-stat-val">#{{ currentUser.id }}</span>
                     <span class="p-stat-label">ID</span>
                   </div>
                   <div class="p-stat-divider"></div>
                   <div class="p-stat">
                     <span class="p-stat-val" style="color:#22c55e">Actif</span>
                     <span class="p-stat-label">Statut</span>
                   </div>
                 </div>
               </div>
             </div>

             <!-- Formulaire droite -->
             <div class="profile-right">
               <div class="profile-edit-card">
                 <h3 class="edit-title">Modifier mon profil</h3>

                 <div class="form-group">
                   <label>Nom complet</label>
                   <div class="input-wrap-profile">
                     <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15">
                       <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                       <circle cx="12" cy="7" r="4"/>
                     </svg>
                     <input v-model="profileForm.nom" placeholder="Votre nom" class="profile-input"/>
                   </div>
                 </div>

                 <div class="form-group">
                   <label>Email</label>
                   <div class="input-wrap-profile">
                     <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15">
                       <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                       <polyline points="22,6 12,13 2,6"/>
                     </svg>
                     <input v-model="profileForm.email" type="email" placeholder="votre@email.com" class="profile-input"/>
                   </div>
                 </div>





                 <div class="profile-actions">
                   <button class="btn-cancel" @click="resetProfileForm">Annuler</button>
                   <button class="btn-confirm" @click="saveProfile" :disabled="isLoading">
                     <span v-if="!isLoading">Sauvegarder</span>
                     <div v-else class="spinner-sm"></div>
                   </button>
                 </div>
               </div>
             </div>
           </div>
         </div>
       </div>
      </transition>
    </main>

    <!-- MODAL CREATE/EDIT -->
    <transition name="modal">
      <div class="modal-overlay" v-if="modal.show" @click.self="modal.show = false">
        <div class="modal-box">
          <div class="modal-header">
            <h3>{{ modal.mode === 'create' ? 'Ajouter un utilisateur' : 'Modifier l\'utilisateur' }}</h3>
            <button class="modal-close" @click="modal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Nom complet</label>
              <input v-model="modal.form.nom" placeholder="Nom complet" class="modal-input"/>
            </div>
            <div class="form-group">
              <label>Email</label>
              <input v-model="modal.form.email" type="email" placeholder="email@exemple.com" class="modal-input"/>
            </div>
            <div class="form-group" v-if="modal.mode === 'create'">
              <label>Mot de passe</label>
              <input v-model="modal.form.password" type="password" placeholder="••••••••" class="modal-input"/>
            </div>
            <div class="form-group">
              <label>Rôle</label>
              <div class="role-selector">
                <button v-for="r in ['STUDENT','TEACHER','ADMIN']" :key="r"
                  :class="['role-opt', { active: modal.form.role === r }]"
                  @click="modal.form.role = r" type="button">
                  {{ r === 'STUDENT' ? '🎒' : r === 'TEACHER' ? '🧑‍🏫' : '⚙️' }}
                  {{ r }}
                </button>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="modal.show = false">Annuler</button>
            <button class="btn-confirm" @click="saveUser" :disabled="isLoading">
              <span v-if="!isLoading">{{ modal.mode === 'create' ? 'Créer' : 'Sauvegarder' }}</span>
              <div v-else class="spinner-sm"></div>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- MODAL DELETE -->
    <transition name="modal">
      <div class="modal-overlay" v-if="deleteModal.show" @click.self="deleteModal.show = false">
        <div class="modal-box modal-sm">
          <div class="modal-header">
            <h3>Confirmer la suppression</h3>
            <button class="modal-close" @click="deleteModal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="delete-confirm">
              <div class="delete-icon">🗑️</div>
              <p>Voulez-vous supprimer <strong>{{ deleteModal.user?.nom }}</strong> ?</p>
              <p class="delete-warn">Cette action est irréversible.</p>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="deleteModal.show = false">Annuler</button>
            <button class="btn-delete" @click="deleteUser" :disabled="isLoading">
              <span v-if="!isLoading">Supprimer</span>
              <div v-else class="spinner-sm"></div>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- PROFILE MODAL -->
    <transition name="modal">
      <div class="modal-overlay" v-if="profileModal.show" @click.self="profileModal.show = false">
        <div class="modal-box">
          <div class="modal-header">
            <h3>Profil utilisateur</h3>
            <button class="modal-close" @click="profileModal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="profile-view">
              <div class="profile-avatar" :style="{ background: roleColor(profileModal.user?.role) }">
                {{ profileModal.user?.nom?.charAt(0)?.toUpperCase() }}
              </div>
              <h3 class="profile-name">{{ profileModal.user?.nom }}</h3>
              <p class="profile-email">{{ profileModal.user?.email }}</p>
              <span :class="['role-badge', profileModal.user?.role?.toLowerCase()]">{{ profileModal.user?.role }}</span>
              <div class="profile-details">
                <div class="detail-row"><span>ID</span><strong>#{{ profileModal.user?.id }}</strong></div>
                <div class="detail-row"><span>Statut</span><strong style="color:#00f5d4">Actif</strong></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Toast -->
    <transition name="toast">
      <div class="toast" v-if="toast.show" :class="toast.type">
        {{ toast.type === 'success' ? '✅' : '❌' }} {{ toast.msg }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api.js'

const sidebarCollapsed = ref(false)
const activeView = ref('overview')
const userTab = ref('students')
const userSearch = ref('')
const globalSearch = ref('')
const isLoading = ref(false)
const allUsers = ref([])
const currentTime = ref('')
const toast = ref({ show: false, msg: '', type: 'success' })

const adminName = computed(() => localStorage.getItem('userName') || 'Admin')
const adminInitial = computed(() => adminName.value?.charAt(0)?.toUpperCase() || 'A')

const modal = ref({ show: false, mode: 'create', form: { nom: '', email: '', password: '', role: 'STUDENT' }, editId: null })
const deleteModal = ref({ show: false, user: null })
const profileModal = ref({ show: false, user: null })

const currentUser = ref({ id: null, nom: '', email: '', role: '', photoUrl: '' })
const profileForm = ref({ nom: '', email: '', photoUrl: '' })


const navItems = computed(() => [
  { id: 'overview',   label: 'Vue globale',  icon: svgGrid },
  { id: 'users',      label: 'Utilisateurs', icon: svgUsers, badge: allUsers.value.length },
  { id: 'classrooms', label: 'Classrooms',   icon: svgClass },
  { id: 'profile',    label: 'Mon Profil',   icon: svgProfile },
])

const svgGrid    = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`
const svgUsers   = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`
const svgClass   = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>`
const svgProfile = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`
const students = computed(() => allUsers.value.filter(u => u.role === 'STUDENT'))
const teachers = computed(() => allUsers.value.filter(u => u.role === 'TEACHER'))
const admins   = computed(() => allUsers.value.filter(u => u.role === 'ADMIN'))
const recentUsers = computed(() => [...allUsers.value].slice(-5).reverse())

const studentPct = computed(() => allUsers.value.length ? (students.value.length / allUsers.value.length) * 100 : 0)
const teacherPct = computed(() => allUsers.value.length ? (teachers.value.length / allUsers.value.length) * 100 : 0)

const currentPageTitle = computed(() => {
  const map = { overview: 'Vue globale', users: 'Utilisateurs', classrooms: 'Classrooms', profile: 'Mon Profil' }
  return map[activeView.value] || ''
})

const statsCards = computed(() => [
  { label: 'Total utilisateurs', val: allUsers.value.length, trend: 12, pct: 75, color: '#00f5d4', bg: 'rgba(0,245,212,0.1)', icon: svgUsers },
  { label: 'Étudiants',          val: students.value.length, trend: 8,  pct: 60, color: '#6366f1', bg: 'rgba(99,102,241,0.1)', icon: svgProfile },
  { label: 'Enseignants',        val: teachers.value.length, trend: 5,  pct: 40, color: '#f59e0b', bg: 'rgba(245,158,11,0.1)', icon: svgClass },
  { label: 'Classrooms',         val: 0,                     trend: 3,  pct: 30, color: '#a855f7', bg: 'rgba(168,85,247,0.1)', icon: svgClass },
])

const userTabs = computed(() => [
  { id: 'all',      label: 'Tous',        icon: '', count: allUsers.value.length },
  { id: 'students', label: 'Étudiants',   icon: '', count: students.value.length },
  { id: 'teachers', label: 'Enseignants', icon: '', count: teachers.value.length },
])

const filteredUsers = computed(() => {
  let list = userTab.value === 'all' ? allUsers.value
    : userTab.value === 'students' ? students.value : teachers.value
  if (userSearch.value)
    list = list.filter(u => u.nom?.toLowerCase().includes(userSearch.value.toLowerCase())
      || u.email?.toLowerCase().includes(userSearch.value.toLowerCase()))
  return list
})

function roleColor(role) {
  if (role === 'TEACHER') return 'linear-gradient(135deg,#6366f1,#8b5cf6)'
  if (role === 'ADMIN')   return 'linear-gradient(135deg,#ef4444,#f97316)'
  return 'linear-gradient(135deg,#00f5d4,#0891b2)'
}

function showToast(msg, type = 'success') {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3000)
}

function openModal(mode, user = null, defaultRole = 'STUDENT') {
  modal.value = {
    show: true, mode,
    editId: user?.id || null,
    form: {
      nom: user?.nom || '',
      email: user?.email || '',
      password: '',
      role: user?.role || defaultRole
    }
  }
}

function openProfile(user) {
  profileModal.value = { show: true, user }
}

function confirmDelete(user) {
  deleteModal.value = { show: true, user }
}

async function loadUsers() {
  try {
    const res = await api.get('/users')
    allUsers.value = res.data
  } catch {
    showToast('Erreur de chargement des utilisateurs', 'error')
  }
}

async function saveUser() {
  isLoading.value = true
  try {
    if (modal.value.mode === 'create') {
      await api.post('/auth/register', modal.value.form)
      showToast('Utilisateur créé avec succès !')
    } else {
      await api.patch(`/users/${modal.value.editId}`, {
        nom: modal.value.form.nom,
        email: modal.value.form.email,
        role: modal.value.form.role
      })
      showToast('Utilisateur modifié avec succès !')
    }
    modal.value.show = false
    await loadUsers()
  } catch {
    showToast('Erreur lors de l\'opération', 'error')
  } finally {
    isLoading.value = false
  }
}

async function deleteUser() {
  isLoading.value = true
  try {
    await api.delete(`/users/${deleteModal.value.user.id}`)
    showToast('Utilisateur supprimé !')
    deleteModal.value.show = false
    await loadUsers()
  } catch {
    showToast('Erreur lors de la suppression', 'error')
  } finally {
    isLoading.value = false
  }
}

function logout() {
  localStorage.clear()
  window.location.href = '/login'
}

function handleSearch() {
  if (globalSearch.value) activeView.value = 'users'
  userSearch.value = globalSearch.value
}

async function loadCurrentUser() {
  try {
    const email = localStorage.getItem('userName')
    const res = await api.get('/users')
    const me = res.data.find(u => u.email === email)
    if (me) {
      currentUser.value = me
      profileForm.value = { nom: me.nom, email: me.email, photoUrl: me.photoUrl || '' }
    }
  } catch {}
}




function resetProfileForm() {
  profileForm.value = {
    nom: currentUser.value.nom,
    email: currentUser.value.email,
    photoUrl: currentUser.value.photoUrl || ''
  }
}

async function saveProfile() {
  isLoading.value = true
  try {
    await api.patch(`/users/${currentUser.value.id}`, {
      nom: profileForm.value.nom,
      email: profileForm.value.email
    })
    currentUser.value = { ...currentUser.value, ...profileForm.value }
    localStorage.setItem('userName', profileForm.value.email)
    showToast('Profil mis à jour avec succès !')
  } catch {
    showToast('Erreur lors de la sauvegarde', 'error')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadUsers()
  loadCurrentUser()
  const tick = () => {
    currentTime.value = new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
  }
  tick()
  setInterval(tick, 60000)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap');

* { box-sizing: border-box; margin: 0; padding: 0; }

.admin-root {
  display: flex;
  min-height: 100vh;
  background: #050b18;
  font-family: 'DM Sans', sans-serif;
  color: #fff;
}

/* SIDEBAR */
.sidebar {
  width: 240px;
  min-height: 100vh;
  background: rgba(255,255,255,0.03);
  border-right: 1px solid rgba(255,255,255,0.07);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4,0,0.2,1);
  position: sticky;
  top: 0;
  height: 100vh;
  overflow: hidden;
}
.sidebar.collapsed { width: 64px; }

.sidebar-header {
  padding: 1.2rem 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.brand { display: flex; align-items: center; gap: 10px; overflow: hidden; }
.brand-icon {
  width: 36px; height: 36px;
  background: rgba(0,245,212,0.08);
  border: 1px solid rgba(0,245,212,0.2);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.brand-text {
  font-family: 'Syne', sans-serif;
  font-size: 1.1rem;
  font-weight: 800;
  white-space: nowrap;
}
.brand-text em { color: #00f5d4; font-style: normal; }

.collapse-btn {
  background: none; border: none; cursor: pointer;
  color: rgba(255,255,255,0.3); padding: 4px;
  border-radius: 6px; transition: all 0.2s;
  flex-shrink: 0;
}
.collapse-btn:hover { background: rgba(255,255,255,0.06); color: #fff; }
.collapse-btn svg { width: 16px; height: 16px; }

.sidebar-nav { flex: 1; padding: 0.8rem 0; overflow-y: auto; }
.nav-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0.65rem 1rem;
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(255,255,255,0.45);
  font-family: 'DM Sans', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 0;
  transition: all 0.2s;
  position: relative;
  white-space: nowrap;
  overflow: hidden;
}
.nav-item:hover { color: #fff; background: rgba(255,255,255,0.04); }
.nav-item.active {
  color: #00f5d4;
  background: rgba(0,245,212,0.07);
  border-right: 2px solid #00f5d4;
}
.nav-icon { flex-shrink: 0; display: flex; }
.nav-badge {
  margin-left: auto;
  background: rgba(0,245,212,0.15);
  color: #00f5d4;
  font-size: 0.7rem;
  padding: 2px 7px;
  border-radius: 10px;
}

.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  gap: 8px;
}
.admin-info { display: flex; align-items: center; gap: 8px; flex: 1; overflow: hidden; }
.admin-avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00f5d4, #6366f1);
  display: flex; align-items: center; justify-content: center;
  font-size: 0.85rem; font-weight: 600; color: #050b18;
  flex-shrink: 0;
}
.admin-name { font-size: 0.82rem; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.admin-role { font-size: 0.72rem; color: rgba(255,255,255,0.35); }
.logout-btn {
  background: none; border: none; cursor: pointer;
  color: rgba(255,255,255,0.3); padding: 6px; border-radius: 8px;
  transition: all 0.2s; flex-shrink: 0;
}
.logout-btn:hover { background: rgba(239,68,68,0.1); color: #ef4444; }

/* MAIN */
.main { flex: 1; display: flex; flex-direction: column; min-width: 0; }

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.2rem 2rem;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  background: rgba(5,11,24,0.8);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 10;
}
.page-title {
  font-family: 'Syne', sans-serif;
  font-size: 1.2rem;
  font-weight: 700;
  color: #fff;
}
.breadcrumb { font-size: 0.75rem; color: rgba(255,255,255,0.3); margin-top: 2px; }
.topbar-right { display: flex; align-items: center; gap: 1rem; }

.search-box {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  padding: 0.5rem 0.8rem;
}
.search-box input {
  background: none; border: none; outline: none;
  color: #fff; font-family: 'DM Sans', sans-serif;
  font-size: 0.85rem; width: 180px;
}
.search-box input::placeholder { color: rgba(255,255,255,0.25); }
.search-box svg { color: rgba(255,255,255,0.3); flex-shrink: 0; }

.topbar-time { font-size: 0.82rem; color: rgba(255,255,255,0.3); font-variant-numeric: tabular-nums; }

/* VIEW */
.view { padding: 1.8rem 2rem; animation: fadeIn 0.35s ease; }
@keyframes fadeIn { from { opacity:0; transform:translateY(8px); } to { opacity:1; transform:none; } }

/* STATS */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.stat-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px;
  padding: 1.2rem;
  animation: fadeIn 0.5s ease both;
  transition: transform 0.2s, border-color 0.2s;
}
.stat-card:hover { transform: translateY(-2px); border-color: rgba(255,255,255,0.12); }
.stat-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.8rem; }
.stat-icon {
  width: 38px; height: 38px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
}
.stat-trend { font-size: 0.75rem; font-weight: 500; }
.stat-trend.up { color: #22c55e; }
.stat-trend.down { color: #ef4444; }
.stat-val { font-family: 'Syne', sans-serif; font-size: 1.8rem; font-weight: 700; margin-bottom: 2px; }
.stat-label { font-size: 0.78rem; color: rgba(255,255,255,0.4); margin-bottom: 0.8rem; }
.stat-bar { height: 3px; background: rgba(255,255,255,0.06); border-radius: 2px; }
.stat-bar-fill { height: 100%; border-radius: 2px; transition: width 0.8s ease; }

/* CHARTS ROW */
.charts-row { display: grid; grid-template-columns: 280px 1fr; gap: 1rem; }
.flex-2 { flex: 2; }
.chart-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px;
  padding: 1.3rem;
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.2rem; }
.card-header h3 { font-family: 'Syne', sans-serif; font-size: 0.95rem; font-weight: 600; }
.btn-text { background: none; border: none; color: #00f5d4; cursor: pointer; font-size: 0.8rem; transition: opacity 0.2s; }
.btn-text:hover { opacity: 0.7; }

/* Donut */
.donut-wrap { position: relative; width: 140px; height: 140px; margin: 0 auto 1rem; }
.donut-svg { width: 100%; height: 100%; transform: rotate(-90deg); }
.donut-center {
  position: absolute; inset: 0;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
}
.donut-total { font-family: 'Syne', sans-serif; font-size: 1.6rem; font-weight: 700; }
.donut-label { font-size: 0.72rem; color: rgba(255,255,255,0.4); }
.donut-legend { display: flex; flex-direction: column; gap: 8px; }
.legend-item { display: flex; align-items: center; gap: 8px; font-size: 0.82rem; color: rgba(255,255,255,0.6); }
.legend-item strong { margin-left: auto; color: #fff; }
.dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }

/* Recent list */
.recent-list { display: flex; flex-direction: column; gap: 8px; }
.recent-item {
  display: flex; align-items: center; gap: 12px;
  padding: 0.7rem 0.8rem;
  border-radius: 10px;
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.04);
  transition: border-color 0.2s;
}
.recent-item:hover { border-color: rgba(255,255,255,0.08); }
.recent-info { flex: 1; min-width: 0; }
.recent-name { font-size: 0.88rem; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.recent-email { font-size: 0.75rem; color: rgba(255,255,255,0.35); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

/* SUB TABS */
.sub-tabs { display: flex; gap: 6px; margin-bottom: 1.2rem; }
.sub-tab {
  display: flex; align-items: center; gap: 6px;
  padding: 0.55rem 1rem;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 10px;
  color: rgba(255,255,255,0.45);
  font-family: 'DM Sans', sans-serif;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}
.sub-tab:hover { color: #fff; border-color: rgba(255,255,255,0.12); }
.sub-tab.active { color: #00f5d4; border-color: rgba(0,245,212,0.3); background: rgba(0,245,212,0.06); }
.count {
  background: rgba(255,255,255,0.08);
  padding: 1px 7px; border-radius: 8px;
  font-size: 0.72rem;
}
.sub-tab.active .count { background: rgba(0,245,212,0.15); color: #00f5d4; }

/* TABLE TOOLBAR */
.table-toolbar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 1rem;
}
.search-inline {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px; padding: 0.5rem 0.8rem;
}
.search-inline svg { color: rgba(255,255,255,0.3); }
.search-inline input {
  background: none; border: none; outline: none;
  color: #fff; font-family: 'DM Sans', sans-serif; font-size: 0.85rem; width: 220px;
}
.search-inline input::placeholder { color: rgba(255,255,255,0.25); }

.btn-add {
  display: flex; align-items: center; gap: 6px;
  padding: 0.55rem 1.1rem;
  background: linear-gradient(135deg, #00f5d4, #00c4aa);
  border: none; border-radius: 10px;
  color: #050b18; font-family: 'Syne', sans-serif;
  font-size: 0.85rem; font-weight: 600; cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-add:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(0,245,212,0.25); }

/* TABLE */
.table-wrap {
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px; overflow: hidden;
}
.data-table { width: 100%; border-collapse: collapse; }
.data-table thead tr {
  border-bottom: 1px solid rgba(255,255,255,0.06);
  background: rgba(255,255,255,0.02);
}
.data-table th {
  padding: 0.8rem 1.1rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255,255,255,0.35);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.table-row { border-bottom: 1px solid rgba(255,255,255,0.04); animation: fadeIn 0.4s ease both; transition: background 0.15s; }
.table-row:last-child { border-bottom: none; }
.table-row:hover { background: rgba(255,255,255,0.02); }
.data-table td { padding: 0.85rem 1.1rem; font-size: 0.88rem; }

.user-cell { display: flex; align-items: center; gap: 10px; }
.user-avatar-sm {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8rem; font-weight: 600; color: #fff;
  flex-shrink: 0;
}
.user-name { font-weight: 500; }
.user-id { font-size: 0.72rem; color: rgba(255,255,255,0.3); }
.email-cell { color: rgba(255,255,255,0.55); font-size: 0.85rem; }

/* BADGES */
.role-badge {
  font-size: 0.7rem; font-weight: 600; padding: 3px 9px;
  border-radius: 6px; letter-spacing: 0.04em;
}
.role-badge.student { background: rgba(0,245,212,0.12); color: #00f5d4; }
.role-badge.teacher { background: rgba(99,102,241,0.12); color: #818cf8; }
.role-badge.admin   { background: rgba(239,68,68,0.12);  color: #f87171; }
.status-badge.active { background: rgba(34,197,94,0.1); color: #22c55e; font-size: 0.75rem; padding: 3px 9px; border-radius: 6px; }

/* ACTION BUTTONS */
.action-btns { display: flex; gap: 5px; }
.action-btn {
  width: 30px; height: 30px; border-radius: 8px; border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s;
}
.action-btn.edit   { background: rgba(99,102,241,0.1);  color: #818cf8; }
.action-btn.view   { background: rgba(0,245,212,0.1);   color: #00f5d4; }
.action-btn.delete { background: rgba(239,68,68,0.1);   color: #f87171; }
.action-btn:hover  { transform: scale(1.1); filter: brightness(1.2); }

/* TEACHER CARDS */
.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 1rem; }
.teacher-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px; padding: 1.3rem;
  animation: fadeIn 0.4s ease both;
  transition: transform 0.2s, border-color 0.2s;
  text-align: center;
}
.teacher-card:hover { transform: translateY(-3px); border-color: rgba(99,102,241,0.25); }
.teacher-card-top { display: flex; justify-content: flex-end; gap: 4px; margin-bottom: 0.8rem; }
.teacher-avatar {
  width: 64px; height: 64px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.5rem; font-weight: 700; color: #fff;
  margin: 0 auto 0.8rem;
}
.teacher-name { font-family: 'Syne', sans-serif; font-size: 0.95rem; font-weight: 600; margin-bottom: 4px; }
.teacher-email { font-size: 0.78rem; color: rgba(255,255,255,0.35); margin-bottom: 0.8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.teacher-meta { display: flex; align-items: center; justify-content: center; gap: 8px; }
.teacher-id { font-size: 0.72rem; color: rgba(255,255,255,0.25); }

/* EMPTY STATE */
.empty-state {
  text-align: center; padding: 2.5rem;
  color: rgba(255,255,255,0.25); font-size: 0.88rem;
}

/* MODAL */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(5,11,24,0.85);
  backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
  z-index: 100;
}
.modal-box {
  background: #0d1b2e;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 20px;
  width: 100%; max-width: 440px;
  animation: modalIn 0.3s cubic-bezier(0.34,1.56,0.64,1);
  overflow: hidden;
}
.modal-sm { max-width: 340px; }
@keyframes modalIn { from { opacity:0; transform:scale(0.9) translateY(20px); } to { opacity:1; transform:none; } }

.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 1.2rem 1.4rem;
  border-bottom: 1px solid rgba(255,255,255,0.07);
}
.modal-header h3 { font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 600; }
.modal-close {
  background: none; border: none; cursor: pointer;
  color: rgba(255,255,255,0.35); font-size: 1rem;
  width: 28px; height: 28px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.modal-close:hover { background: rgba(255,255,255,0.06); color: #fff; }
.modal-body { padding: 1.4rem; }

.form-group { margin-bottom: 1rem; }
.form-group label { display: block; font-size: 0.8rem; color: rgba(255,255,255,0.5); margin-bottom: 6px; font-weight: 500; }
.modal-input {
  width: 100%; padding: 0.65rem 0.9rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px; color: #fff;
  font-family: 'DM Sans', sans-serif; font-size: 0.88rem;
  outline: none; transition: border-color 0.2s;
}
.modal-input:focus { border-color: rgba(0,245,212,0.4); }
.modal-input::placeholder { color: rgba(255,255,255,0.2); }

.role-selector { display: flex; gap: 6px; }
.role-opt {
  flex: 1; padding: 0.6rem 0.3rem;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px; color: rgba(255,255,255,0.45);
  font-family: 'DM Sans', sans-serif; font-size: 0.75rem;
  cursor: pointer; transition: all 0.2s; text-align: center;
}
.role-opt:hover { border-color: rgba(255,255,255,0.15); color: #fff; }
.role-opt.active { border-color: #00f5d4; background: rgba(0,245,212,0.08); color: #00f5d4; }

.modal-footer {
  display: flex; gap: 8px; justify-content: flex-end;
  padding: 1rem 1.4rem;
  border-top: 1px solid rgba(255,255,255,0.07);
}
.btn-cancel {
  padding: 0.6rem 1.1rem; background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1); border-radius: 10px;
  color: rgba(255,255,255,0.6); font-family: 'DM Sans', sans-serif;
  font-size: 0.88rem; cursor: pointer; transition: all 0.2s;
}
.btn-cancel:hover { background: rgba(255,255,255,0.08); color: #fff; }
.btn-confirm {
  padding: 0.6rem 1.3rem;
  background: linear-gradient(135deg, #00f5d4, #00c4aa);
  border: none; border-radius: 10px;
  color: #050b18; font-family: 'Syne', sans-serif;
  font-size: 0.88rem; font-weight: 600; cursor: pointer;
  transition: all 0.2s; min-width: 90px;
  display: flex; align-items: center; justify-content: center;
}
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(0,245,212,0.25); }
.btn-delete {
  padding: 0.6rem 1.3rem;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  border: none; border-radius: 10px;
  color: #fff; font-family: 'Syne', sans-serif;
  font-size: 0.88rem; font-weight: 600; cursor: pointer;
  min-width: 90px; display: flex; align-items: center; justify-content: center;
}

/* Delete modal */
.delete-confirm { text-align: center; }
.delete-icon { font-size: 2.5rem; margin-bottom: 0.8rem; }
.delete-confirm p { color: rgba(255,255,255,0.7); font-size: 0.9rem; line-height: 1.5; }
.delete-warn { color: rgba(239,68,68,0.7) !important; font-size: 0.8rem !important; margin-top: 4px; }

/* Profile */
.profile-view { text-align: center; padding: 0.5rem 0; }
.profile-avatar {
  width: 80px; height: 80px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 2rem; font-weight: 700; color: #fff;
  margin: 0 auto 1rem;
}
.profile-name { font-family: 'Syne', sans-serif; font-size: 1.2rem; font-weight: 700; margin-bottom: 4px; }
.profile-email { color: rgba(255,255,255,0.4); font-size: 0.85rem; margin-bottom: 0.8rem; }
.profile-details { margin-top: 1.2rem; text-align: left; display: flex; flex-direction: column; gap: 6px; }
.detail-row { display: flex; justify-content: space-between; font-size: 0.85rem; padding: 8px 12px; background: rgba(255,255,255,0.03); border-radius: 8px; }
.detail-row span { color: rgba(255,255,255,0.4); }

/* Spinner */
.spinner-sm {
  width: 16px; height: 16px;
  border: 2px solid rgba(0,0,0,0.2);
  border-top-color: #050b18;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* TOAST */
.toast {
  position: fixed; bottom: 1.5rem; right: 1.5rem;
  padding: 0.75rem 1.2rem; border-radius: 12px;
  font-size: 0.85rem; z-index: 200;
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}
.toast.success { background: rgba(0,245,212,0.1); border: 1px solid rgba(0,245,212,0.25); color: #00f5d4; }
.toast.error   { background: rgba(239,68,68,0.1);  border: 1px solid rgba(239,68,68,0.25);  color: #f87171; }

/* TRANSITIONS */
.fade-enter-active, .fade-leave-active { transition: all 0.25s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(8px); }
.modal-enter-active, .modal-leave-active { transition: all 0.25s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateY(10px); }

@media (max-width: 1100px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) {
  .charts-row { grid-template-columns: 1fr; }
  .sidebar { position: fixed; z-index: 50; height: 100vh; }
}
/* PROFILE */
.profile-page { width: 100%;
 max-width: 100%; }

.profile-cover {
  height: 180px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(0,245,212,0.15), rgba(99,102,241,0.2), rgba(168,85,247,0.15));
  border: 1px solid rgba(255,255,255,0.08);
  position: relative;
  margin-bottom: 70px;
  width: 100%;
}

.profile-avatar-wrap {
  position: absolute;
  bottom: -44px;
  left: 2rem;
  display: flex;
  align-items: flex-end;
  gap: 14px;
}

.profile-avatar-ring {
  width: 88px; height: 88px;
  border-radius: 50%;
  border: 3px solid #00f5d4;
  background: #0d1b2e;
  overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}

.profile-avatar-img { width: 100%; height: 100%; object-fit: cover; }

.profile-avatar-placeholder {
  font-family: 'Syne', sans-serif;
  font-size: 2rem; font-weight: 700; color: #00f5d4;
}

.change-photo-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 14px;
  background: rgba(0,245,212,0.1);
  border: 1px solid rgba(0,245,212,0.25);
  border-radius: 8px;
  color: #00f5d4; font-size: 0.78rem;
  cursor: pointer; transition: all 0.2s;
  margin-bottom: 6px;
}
.change-photo-btn:hover { background: rgba(0,245,212,0.18); }

.profile-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 1.5rem;
  width: 100%;
}

.profile-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px;
  padding: 1.5rem;
  text-align: center;
}

.profile-avatar-display { margin-bottom: 1rem; }
.profile-avatar-img-lg { width: 72px; height: 72px; border-radius: 50%; object-fit: cover; }
.profile-avatar-placeholder-lg {
  width: 72px; height: 72px; border-radius: 50%;
  background: linear-gradient(135deg, #00f5d4, #6366f1);
  display: flex; align-items: center; justify-content: center;
  font-size: 1.8rem; font-weight: 700; color: #050b18;
  margin: 0 auto;
}

.profile-name-display {
  font-family: 'Syne', sans-serif;
  font-size: 1.1rem; font-weight: 700; margin-bottom: 4px;
}
.profile-email-display { font-size: 0.82rem; color: rgba(255,255,255,0.4); margin-bottom: 0.8rem; }

.profile-stats-row {
  display: flex; align-items: center; justify-content: center;
  gap: 1.5rem; margin-top: 1.2rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(255,255,255,0.07);
}
.p-stat { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.p-stat-val { font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700; }
.p-stat-label { font-size: 0.72rem; color: rgba(255,255,255,0.35); }
.p-stat-divider { width: 1px; height: 30px; background: rgba(255,255,255,0.1); }

.profile-edit-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 16px;
  padding: 1.8rem;
  width: 100%;
}
.edit-title {
  font-family: 'Syne', sans-serif;
  font-size: 1rem; font-weight: 600; margin-bottom: 1.3rem;
}

.input-wrap-profile {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px; padding: 0.65rem 0.9rem;
  transition: border-color 0.2s;
}
.input-wrap-profile:focus-within { border-color: rgba(0,245,212,0.4); }
.input-wrap-profile svg { color: rgba(255,255,255,0.3); flex-shrink: 0; }
.profile-input {
  background: none; border: none; outline: none;
  color: #fff; font-family: 'DM Sans', sans-serif;
  font-size: 0.88rem; flex: 1;
}
.profile-input::placeholder { color: rgba(255,255,255,0.2); }

.photo-preview {
  display: flex; align-items: center; gap: 12px;
  padding: 0.8rem; margin-bottom: 1rem;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 10px;
}
.photo-preview img { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; }
.photo-preview-info { display: flex; flex-direction: column; gap: 4px; }
.photo-preview-info span { font-size: 0.78rem; color: rgba(255,255,255,0.4); }
.remove-photo {
  background: rgba(239,68,68,0.1);
  border: 1px solid rgba(239,68,68,0.2);
  color: #f87171; font-size: 0.75rem;
  padding: 3px 8px; border-radius: 6px; cursor: pointer;
  width: fit-content;
}

.profile-actions {
  display: flex; gap: 8px;
  justify-content: flex-end; margin-top: 1.2rem;
}



.change-btn {
  background: rgba(0,245,212,0.2);
  border: 1px solid rgba(0,245,212,0.4);
  color: #00f5d4;
}
.change-btn:hover { background: rgba(0,245,212,0.3); }
.remove-btn {
  background: rgba(239,68,68,0.2);
  border: 1px solid rgba(239,68,68,0.4);
  color: #f87171;
}
.remove-btn:hover { background: rgba(239,68,68,0.3); }
@media (max-width: 900px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
  .profile-cover {
    height: 140px;
    margin-bottom: 80px;
  }
}

</style>