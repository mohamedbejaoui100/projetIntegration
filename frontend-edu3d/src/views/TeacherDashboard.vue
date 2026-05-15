<template>
  <div class="teacher-root">

    <!-- SIDEBAR -->
    <aside :class="['sidebar', { collapsed: sidebarCollapsed }]">
      <div class="sidebar-header">
        <div class="brand">
          <div class="brand-icon">
            <svg width="20" height="20" viewBox="0 0 38 38" fill="none">
              <polygon points="19,2 36,32 2,32" fill="none" stroke="#a78bfa" stroke-width="2.2"/>
              <circle cx="19" cy="19" r="4" fill="#a78bfa"/>
            </svg>
          </div>
          <span class="brand-text" v-if="!sidebarCollapsed">EDU<em>3D</em></span>
        </div>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15">
            <path v-if="!sidebarCollapsed" d="M15 18l-6-6 6-6"/>
            <path v-else d="M9 18l6-6-6-6"/>
          </svg>
        </button>
      </div>

      <div class="teacher-badge" v-if="!sidebarCollapsed">
        <span class="badge-dot"></span>
        Espace Enseignant
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section-label" v-if="!sidebarCollapsed">PRINCIPAL</div>
        <button v-for="item in mainNav" :key="item.id"
          :class="['nav-item', { active: activeView === item.id }]"
          @click="activeView = item.id" :title="item.label">
          <span class="nav-icon" v-html="item.icon"></span>
          <span class="nav-label" v-if="!sidebarCollapsed">{{ item.label }}</span>
          <span class="nav-badge" v-if="item.badge && !sidebarCollapsed">{{ item.badge }}</span>
        </button>

        <div class="nav-section-label" v-if="!sidebarCollapsed" style="margin-top:1rem">CONTENU</div>
        <button v-for="item in contentNav" :key="item.id"
          :class="['nav-item', { active: activeView === item.id }]"
          @click="activeView = item.id" :title="item.label">
          <span class="nav-icon" v-html="item.icon"></span>
          <span class="nav-label" v-if="!sidebarCollapsed">{{ item.label }}</span>
          <span class="nav-badge" v-if="item.badge && !sidebarCollapsed">{{ item.badge }}</span>
        </button>
      </nav>

      <div class="sidebar-footer" v-if="!sidebarCollapsed">
        <div class="teacher-info" @click="activeView = 'profile'" style="cursor:pointer">
          <div class="teacher-avatar-sm">{{ teacherInitial }}</div>
          <div class="teacher-details">
            <div class="teacher-name-sm">{{ currentTeacher.nom || 'Enseignant' }}</div>
            <div class="teacher-role-sm">Teacher</div>
          </div>
        </div>
        <button class="logout-btn" @click="logout">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="17" height="17">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16,17 21,12 16,7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
      </div>
    </aside>

    <!-- MAIN -->
    <main class="main">
      <header class="topbar">
        <div class="topbar-left">
          <h1 class="page-title">{{ currentPageTitle }}</h1>
          <div class="breadcrumb">Teacher / {{ currentPageTitle }}</div>
        </div>
        <div class="topbar-right">
          <div class="search-box">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            <input v-model="searchQuery" placeholder="Rechercher..."/>
          </div>
          <div class="topbar-time">{{ currentTime }}</div>
        </div>
      </header>

      <transition name="fade" mode="out-in">

        <!-- ====== OVERVIEW ====== -->
        <div v-if="activeView === 'overview'" key="overview" class="view">

          <div class="welcome-banner">
            <div class="welcome-left">
              <p class="welcome-sub">Bienvenue de retour </p>
              <h2 class="welcome-name">{{ currentTeacher.nom || 'Enseignant' }}</h2>
              <p class="welcome-desc">Vous avez <strong>{{ myCourses.length }}</strong> cours actifs et <strong>{{ totalStudents }}</strong> étudiants inscrits.</p>
            </div>
            <div class="welcome-right">
              <div class="welcome-3d">
                <svg viewBox="0 0 120 120" width="110" height="110">
                  <polygon points="60,10 110,85 10,85" fill="none" stroke="rgba(167,139,250,0.4)" stroke-width="2"/>
                  <polygon points="60,25 95,80 25,80" fill="rgba(167,139,250,0.08)" stroke="rgba(167,139,250,0.25)" stroke-width="1.5"/>
                  <circle cx="60" cy="60" r="8" fill="rgba(167,139,250,0.3)" stroke="#a78bfa" stroke-width="1.5"/>
                  <circle cx="60" cy="60" r="3" fill="#a78bfa"/>
                  <line x1="60" y1="10" x2="60" y2="60" stroke="rgba(167,139,250,0.3)" stroke-width="1"/>
                  <line x1="110" y1="85" x2="60" y2="60" stroke="rgba(167,139,250,0.3)" stroke-width="1"/>
                  <line x1="10" y1="85" x2="60" y2="60" stroke="rgba(167,139,250,0.3)" stroke-width="1"/>
                </svg>
              </div>
            </div>
          </div>

          <!-- Stats -->
          <div class="stats-grid">
            <div class="stat-card" v-for="(s,i) in statsCards" :key="s.label" :style="{ animationDelay: i*0.08+'s' }">
              <div class="stat-top">
                <div class="stat-icon-wrap" :style="{ background: s.bg }">
                  <span v-html="s.icon"></span>
                </div>
                <span :class="['trend', s.up ? 'up' : 'neutral']">{{ s.up ? '↑' : '—' }} {{ s.trend }}</span>
              </div>
              <div class="stat-num">{{ s.val }}</div>
              <div class="stat-lbl">{{ s.label }}</div>
              <div class="stat-bar"><div class="stat-bar-inner" :style="{ width: s.pct+'%', background: s.color }"></div></div>
            </div>
          </div>

          <!-- Recent courses + classrooms -->
          <div class="overview-row">
            <div class="ov-card">
              <div class="ov-card-header">
                <h3>Mes cours récents</h3>
                <button class="btn-link" @click="activeView = 'courses'">Voir tous →</button>
              </div>
              <div class="course-mini-list">
                <div class="course-mini" v-for="c in myCourses.slice(0,4)" :key="c.id" @click="openCourseDetail(c)">
                  <div class="course-mini-icon">📖</div>
                  <div class="course-mini-info">
                    <div class="course-mini-title">{{ c.title }}</div>
                    <div class="course-mini-desc">{{ c.description || 'Aucune description' }}</div>
                  </div>
                  <div class="course-mini-slides">{{ c.slideCount || 0 }} slides</div>
                </div>
                <div class="empty-state" v-if="!myCourses.length">Aucun cours — créez-en un !</div>
              </div>
            </div>

            <div class="ov-card">
              <div class="ov-card-header">
                <h3>Mes classes</h3>
                <button class="btn-link" @click="activeView = 'classroom'">Voir toutes →</button>
              </div>
              <div class="class-mini-list">
                <div class="class-mini" v-for="cls in myClassrooms.slice(0,4)" :key="cls.id">
                  <div class="class-mini-left">
                    <div class="class-mini-icon">🏛️</div>
                    <div>
                      <div class="class-mini-name">{{ cls.name }}</div>
                      <div class="class-mini-code">Code : <strong>{{ cls.inviteCode }}</strong></div>
                    </div>
                  </div>
                  <div class="class-mini-count">{{ cls.studentCount || 0 }} 🎒</div>
                </div>
                <div class="empty-state" v-if="!myClassrooms.length">Aucune classe — créez-en une !</div>
              </div>
            </div>
          </div>
        </div>

        <!-- ====== COURSES ====== -->
        <div v-else-if="activeView === 'courses'" key="courses" class="view">
          <div class="table-toolbar">
            <div class="search-inline">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
              <input v-model="courseSearch" placeholder="Rechercher un cours..."/>
            </div>
            <button class="btn-primary" @click="openCourseModal('create')">
              + Nouveau cours
            </button>
          </div>

          <div class="courses-grid">
            <div class="course-card" v-for="(course, i) in filteredCourses" :key="course.id"
              :style="{ animationDelay: i*0.06+'s' }"
              @click="openCourseDetail(course)"
              style="cursor:pointer">
              <div class="course-card-header">
                <div class="course-card-icon">📚</div>
                <div class="course-card-actions">
                  <button class="icon-btn edit" @click="openCourseModal('edit', course)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="icon-btn slides" @click="openSlides(course)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                  </button>
                  <button class="icon-btn delete" @click="confirmDeleteCourse(course)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13"><polyline points="3,6 5,6 21,6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>
                  </button>
                </div>
              </div>
              <h4 class="course-card-title">{{ course.title }}</h4>
              <p class="course-card-desc">{{ course.description || 'Aucune description' }}</p>
              <div class="course-card-footer">
                <span class="slide-count">🗂 {{ course.slideCount || 0 }} slides</span>
                <button class="btn-view-slides" @click="openSlides(course)">Gérer slides →</button>
              </div>
            </div>
            <div class="add-course-card" @click="openCourseModal('create')">
              <div class="add-icon">＋</div>
              <span>Nouveau cours</span>
            </div>
          </div>
        </div>

        <!-- ====== CLASSROOM ====== -->
        <div v-else-if="activeView === 'classroom'" key="classroom" class="view">
          <div class="table-toolbar">
            <div class="search-inline">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
              <input v-model="classSearch" placeholder="Rechercher une classe..."/>
            </div>
            <button class="btn-primary" @click="openClassModal('create')">
              + Créer une classe
            </button>
          </div>

          <div class="classrooms-grid">
            <div class="classroom-card" v-for="(cls, i) in filteredClassrooms" :key="cls.id"
              :style="{ animationDelay: i*0.07+'s' }" @click="openClassDetail(cls)">
              <div class="cls-top">
                <div class="cls-avatar">🏛️</div>
                <div class="cls-actions" @click.stop>
                  <button class="icon-btn edit" @click="openClassModal('edit', cls)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button class="icon-btn delete" @click.stop="confirmDeleteClass(cls)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13"><polyline points="3,6 5,6 21,6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>
                  </button>
                </div>
              </div>
              <h4 class="cls-name">{{ cls.name }}</h4>
              <div class="cls-course-tag" v-if="getCourseOfClass(cls)">
                📚 {{ getCourseOfClass(cls)?.title }}
              </div>
              <div class="cls-course-tag" style="color:rgba(255,255,255,0.25)" v-else>
                ⚠️ Aucun cours associé
              </div>

              <!-- Invite Code + QR -->
              <div class="invite-section" @click.stop>
                <div class="invite-code-box">
                  <div class="invite-label">Code d'invitation</div>
                  <div class="invite-code">{{ cls.inviteCode }}</div>
                  <button class="copy-btn" @click="copyCode(cls.inviteCode)">
                    📋 Copier
                  </button>
                </div>
                <button class="qr-btn" @click.stop="showQR(cls)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                  QR Code
                </button>
              </div>

              <div class="cls-footer">
                <span class="cls-students">🎒 {{ cls.studentCount || 0 }} étudiants</span>
                <span class="cls-detail-btn">Voir détails →</span>
              </div>
            </div>

            <div class="add-class-card" @click="openClassModal('create')">
              <div class="add-icon">＋</div>
              <span>Nouvelle classe</span>
            </div>
          </div>
        </div>

        <!-- ====== PROFILE ====== -->
        <div v-else-if="activeView === 'profile'" key="profile" class="view">
          <div class="profile-page">
            <div class="profile-cover">
              <div class="profile-cover-bg"></div>
              <div class="profile-avatar-wrap">
                <div class="profile-ring">
                  <div class="profile-avatar-lg">{{ teacherInitial }}</div>
                </div>
              </div>
            </div>

            <div class="profile-content">
              <div class="profile-left">
                <div class="profile-info-card">
                  <div class="pi-avatar">{{ teacherInitial }}</div>
                  <h3 class="pi-name">{{ currentTeacher.nom }}</h3>
                  <p class="pi-email">{{ currentTeacher.email }}</p>
                  <span class="role-badge-teacher">TEACHER</span>
                  <div class="pi-stats">
                    <div class="pi-stat"><span class="pi-val">{{ myCourses.length }}</span><span class="pi-lbl">Cours</span></div>
                    <div class="pi-div"></div>
                    <div class="pi-stat"><span class="pi-val">{{ myClassrooms.length }}</span><span class="pi-lbl">Classes</span></div>
                    <div class="pi-div"></div>
                    <div class="pi-stat"><span class="pi-val">{{ totalStudents }}</span><span class="pi-lbl">Étudiants</span></div>
                  </div>
                </div>
              </div>

              <div class="profile-right">
                <div class="profile-edit-card">
                  <h3 class="edit-title"> Modifier mon profil</h3>

                  <div class="form-group">
                    <label>Nom complet</label>
                    <div class="input-row">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                      <input v-model="profileForm.nom" placeholder="Votre nom" class="t-input"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <label>Email</label>
                    <div class="input-row">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                      <input v-model="profileForm.email" type="email" placeholder="votre@email.com" class="t-input"/>
                    </div>
                  </div>

                  <div class="profile-actions">
                    <button class="btn-cancel" @click="resetProfile">Annuler</button>
                    <button class="btn-primary" @click="saveProfile" :disabled="isLoading">
                      <span v-if="!isLoading"> Sauvegarder</span>
                      <div v-else class="spin"></div>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </transition>
    </main>

    <!-- ===== MODAL COURSE ===== -->
    <CourseCreator
      :show="courseModal.show"
      :mode="courseModal.mode"
      :initialData="courseModal.initialData || {}"
      @close="courseModal.show = false"
      @submit="handleCourseSubmit"
    />

    <!-- ===== MODAL SLIDES ===== -->
    <transition name="modal">
      <div class="modal-overlay" v-if="slidesModal.show" @click.self="slidesModal.show = false">
        <div class="modal-box modal-lg">
          <div class="modal-header">
            <h3>🗂 Slides — {{ slidesModal.course?.title }}</h3>
            <button class="modal-close" @click="slidesModal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <!-- Add slide form -->
            <div class="slide-add-form">
              <h4 style="margin-bottom:0.8rem;font-size:0.9rem;color:rgba(255,255,255,0.6)">Ajouter une slide</h4>
              <div class="slide-form-grid">
                <div class="form-group">
                  <label>Titre</label>
                  <input v-model="newSlide.title" class="modal-input" placeholder="Titre de la slide"/>
                </div>
                <div class="form-group">
                  <label>Position</label>
                  <input v-model.number="newSlide.position" type="number" class="modal-input" placeholder="0"/>
                </div>
              </div>
              <div class="form-group">
                <label>Contenu texte</label>
                <textarea v-model="newSlide.contentText" class="modal-input modal-textarea" placeholder="Contenu de la slide..."></textarea>
              </div>
              <div class="form-group">
                <label>URL objet 3D (.glb / .gltf)</label>
                <input v-model="newSlide.object3dUrl" class="modal-input" placeholder="https://...model.glb"/>
              </div>
              <button class="btn-primary" @click="addSlide" :disabled="isLoading" style="width:100%">
                <span v-if="!isLoading">＋ Ajouter la slide</span>
                <div v-else class="spin"></div>
              </button>
            </div>

            <!-- Slides list -->
            <div class="slides-list" v-if="currentSlides.length">
              <h4 style="margin-bottom:0.8rem;font-size:0.9rem;color:rgba(255,255,255,0.6)">Slides existantes ({{ currentSlides.length }})</h4>
              <div class="slide-item" v-for="s in currentSlides" :key="s.id">
                <div class="slide-pos">#{{ s.position }}</div>
                <div class="slide-info">
                  <div class="slide-title">{{ s.title }}</div>
                  <div class="slide-content">{{ s.contentText?.substring(0,60) }}{{ s.contentText?.length > 60 ? '...' : '' }}</div>
                </div>
                <div class="slide-3d-tag" v-if="s.object3dUrl">🧊 3D</div>
                <button class="icon-btn delete" @click="deleteSlide(s.id)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="13" height="13"><polyline points="3,6 5,6 21,6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>
                </button>
              </div>
            </div>
            <div class="empty-state" v-else style="margin-top:1rem">Aucune slide — ajoutez-en une !</div>
          </div>
        </div>
      </div>
    </transition>

<!-- ===== MODAL COURSE DETAIL ===== -->
<transition name="modal">
  <div class="modal-overlay" v-if="showCourseDetail" @click.self="showCourseDetail = false">
    <div class="modal-box modal-xl">
      <div class="modal-header">
        <div style="display:flex;align-items:center;gap:10px">
          <span style="font-size:1.5rem">📚</span>
          <div>
            <h3>{{ selectedCourse?.title }}</h3>
            <p style="font-size:0.75rem;color:rgba(255,255,255,0.35);margin-top:2px">
              {{ selectedCourseSlides.length }} slide(s) · par {{ currentTeacher.nom }}
            </p>
          </div>
        </div>
        <div style="display:flex;gap:8px;align-items:center">
          <button class="icon-btn edit" @click="openCourseModal('edit', selectedCourse); showCourseDetail = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="icon-btn slides" @click="openSlides(selectedCourse); showCourseDetail = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
          </button>
          <button class="modal-close" @click="showCourseDetail = false">✕</button>
        </div>
      </div>

      <div class="modal-body" style="padding:0;overflow:hidden">
        <div class="course-detail-layout">

          <!-- Slides sidebar -->
          <div class="slides-sidebar">
            <div class="slides-sidebar-header">
              <span>Slides ({{ selectedCourseSlides.length }})</span>
              <button class="btn-primary" style="padding:4px 12px;font-size:0.75rem"
                @click="openSlides(selectedCourse); showCourseDetail = false">
                + Ajouter
              </button>
            </div>

            <div class="slides-sidebar-list">
              <div
                v-for="slide in selectedCourseSlides" :key="slide.id"
                :class="['slide-thumb', { active: activeSlide?.id === slide.id }]"
                @click="selectSlide(slide)">
                <div class="slide-thumb-pos">#{{ slide.position }}</div>
                <div class="slide-thumb-info">
                  <div class="slide-thumb-title">{{ slide.title }}</div>
                  <div class="slide-thumb-preview">
                    {{ slide.contentText?.substring(0,40) || 'Pas de contenu' }}...
                  </div>
                </div>
                <div class="slide-thumb-3d" v-if="slide.object3dUrl">🧊</div>
              </div>

              <div class="empty-state" v-if="!selectedCourseSlides.length">
                <p>Aucune slide</p>
                <button class="btn-primary" style="margin-top:8px"
                  @click="openSlides(selectedCourse); showCourseDetail = false">
                  Créer la première slide
                </button>
              </div>
            </div>
          </div>

          <!-- Main content -->
          <div class="course-detail-main">

            <!-- Si slide sélectionnée -->
            <div v-if="activeSlide" class="slide-view">
              <!-- 3D Viewer -->
              <div class="slide-3d-area" v-if="activeSlide.object3dUrl">
                <ThreeViewer
                  :modelUrl="activeSlide.object3dUrl"
                  :autoRotate="true"
                />
              </div>

              <!-- Infos slide -->
              <div class="slide-info-panel">
                <div class="slide-info-header">
                  <h3 class="slide-info-title">{{ activeSlide.title }}</h3>
                  <span class="slide-info-pos">Slide {{ activeSlide.position + 1 }} / {{ selectedCourseSlides.length }}</span>
                </div>
                <p class="slide-info-content">{{ activeSlide.contentText || 'Aucun contenu textuel' }}</p>

                <!-- Navigation entre slides -->
                <div class="slide-nav">
                  <button class="slide-nav-btn"
                    :disabled="activeSlide.position === 0"
                    @click="navigateSlide(-1)">
                    ← Précédente
                  </button>
                  <div class="slide-dots">
                    <span v-for="s in selectedCourseSlides" :key="s.id"
                      :class="['dot', { active: s.id === activeSlide.id }]"
                      @click="selectSlide(s)">
                    </span>
                  </div>
                  <button class="slide-nav-btn"
                    :disabled="activeSlide.position === selectedCourseSlides.length - 1"
                    @click="navigateSlide(1)">
                    Suivante →
                  </button>
                </div>
              </div>
            </div>

            <!-- Si aucune slide sélectionnée -->
            <div v-else class="course-detail-empty">
              <div style="font-size:3rem;margin-bottom:1rem">📚</div>
              <h3>{{ selectedCourse?.title }}</h3>
              <p style="color:rgba(255,255,255,0.4);margin-top:8px;max-width:400px;text-align:center">
                {{ selectedCourse?.description || 'Aucune description' }}
              </p>
              <p style="color:rgba(255,255,255,0.25);font-size:0.85rem;margin-top:1rem">
                {{ selectedCourseSlides.length > 0 ? '← Sélectionnez une slide pour commencer' : 'Aucune slide pour ce cours' }}
              </p>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</transition>


    <!-- ===== MODAL CLASSROOM ===== -->
    <transition name="modal">
      <div class="modal-overlay" v-if="classModal.show" @click.self="classModal.show = false">
        <div class="modal-box">
          <div class="modal-header">
            <h3>{{ classModal.mode === 'create' ? '🏛️ Nouvelle classe' : ' Modifier la classe' }}</h3>
            <button class="modal-close" @click="classModal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Nom de la classe</label>
              <input v-model="classModal.form.name" class="modal-input" placeholder="Ex: Classe A - Maths 3D"/>
            </div>
            <div class="form-group">
              <label>Cours associé</label>
              <select v-model="classModal.form.courseId" class="modal-input">
                <option value="">-- Sélectionner un cours --</option>
                <option v-for="c in myCourses" :key="c.id" :value="c.id">{{ c.title }}</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="classModal.show = false">Annuler</button>
            <button class="btn-primary" @click="saveClassroom" :disabled="isLoading">
              <span v-if="!isLoading">{{ classModal.mode === 'create' ? 'Créer' : 'Sauvegarder' }}</span>
              <div v-else class="spin"></div>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- ===== MODAL CLASS DETAIL ===== -->
    <transition name="modal">
      <div class="modal-overlay" v-if="classDetailModal.show" @click.self="classDetailModal.show = false">
        <div class="modal-box modal-lg">
          <div class="modal-header">
            <h3>🏛️ {{ classDetailModal.cls?.name }}</h3>
            <button class="modal-close" @click="classDetailModal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="cls-detail-grid">
              <!-- Code + QR -->
              <div class="detail-section">
                <h4 class="section-title">🔑 Invitation</h4>
                <div class="big-code">{{ classDetailModal.cls?.inviteCode }}</div>
                <button class="copy-btn-lg" @click="copyCode(classDetailModal.cls?.inviteCode)">📋 Copier le code</button>
                <div class="qr-container" v-if="qrDataUrl">
                  <img :src="qrDataUrl" alt="QR Code" class="qr-img"/>
                  <p class="qr-hint">Les étudiants scannent ce QR pour rejoindre</p>
                  <a :href="qrDataUrl" download="qrcode.png" class="qr-download"> Télécharger QR</a>
                </div>
              </div>

              <!-- Students -->
              <div class="detail-section">
                <h4 class="section-title">🎒 Étudiants inscrits ({{ classStudents.length }})</h4>
                <div class="students-scroll">
                  <div class="student-row" v-for="s in classStudents" :key="s.id">
                    <div class="s-avatar">{{ s.nom?.charAt(0)?.toUpperCase() }}</div>
                    <div class="s-info">
                      <div class="s-name">{{ s.nom }}</div>
                      <div class="s-email">{{ s.email }}</div>
                    </div>
                  </div>
                  <div class="empty-state" v-if="!classStudents.length">Aucun étudiant inscrit</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- ===== MODAL QR ===== -->
    <transition name="modal">
      <div class="modal-overlay" v-if="qrModal.show" @click.self="qrModal.show = false">
        <div class="modal-box modal-sm" style="text-align:center">
          <div class="modal-header">
            <h3>QR Code — {{ qrModal.cls?.name }}</h3>
            <button class="modal-close" @click="qrModal.show = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="big-code" style="margin-bottom:1rem">{{ qrModal.cls?.inviteCode }}</div>
            <div class="qr-container">
              <img v-if="qrDataUrl" :src="qrDataUrl" alt="QR Code" class="qr-img"/>
              <p class="qr-hint">Scannez pour rejoindre la classe</p>
              <div style="display:flex;gap:8px;justify-content:center;margin-top:0.8rem">
                <button class="copy-btn-lg" @click="copyCode(qrModal.cls?.inviteCode)">📋 Copier le code</button>
                <a v-if="qrDataUrl" :href="qrDataUrl" download="qrcode.png" class="qr-download"> Télécharger</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- ===== MODAL DELETE ===== -->
    <transition name="modal">
      <div class="modal-overlay" v-if="deleteModal.show" @click.self="deleteModal.show = false">
        <div class="modal-box modal-sm">
          <div class="modal-header">
            <h3>Confirmer la suppression</h3>
            <button class="modal-close" @click="deleteModal.show = false">✕</button>
          </div>
          <div class="modal-body" style="text-align:center">
            <div style="font-size:2.5rem;margin-bottom:0.8rem">🗑️</div>
            <p style="color:rgba(255,255,255,0.7)">Supprimer <strong>{{ deleteModal.name }}</strong> ?</p>
            <p style="color:rgba(239,68,68,0.7);font-size:0.8rem;margin-top:4px">Cette action est irréversible.</p>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="deleteModal.show = false">Annuler</button>
            <button class="btn-delete" @click="deleteModal.action" :disabled="isLoading">
              <span v-if="!isLoading">Supprimer</span>
              <div v-else class="spin"></div>
            </button>
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
import CourseCreator from './CourseCreator.vue'
import ThreeViewer from './ThreeViewer.vue'


// ─── State ───────────────────────────────────────────────
const sidebarCollapsed = ref(false)
const activeView = ref('overview')
const isLoading = ref(false)
const currentTime = ref('')
const searchQuery = ref('')
const courseSearch = ref('')
const classSearch  = ref('')

const currentTeacher = ref({ id: null, nom: '', email: '', role: '' })
const myCourses      = ref([])
const myClassrooms   = ref([])
const currentSlides  = ref([])
const classStudents  = ref([])
const qrDataUrl      = ref('')

const toast = ref({ show: false, msg: '', type: 'success' })

const courseModal = ref({ show: false, mode: 'create', form: { title: '', description: '' }, editId: null })
const classModal  = ref({ show: false, mode: 'create', form: { name: '', courseId: '' }, editId: null })
const slidesModal = ref({ show: false, course: null })
const classDetailModal = ref({ show: false, cls: null })
const qrModal     = ref({ show: false, cls: null })
const deleteModal = ref({ show: false, name: '', action: () => {} })
const profileForm = ref({ nom: '', email: '' })

const selectedCourse = ref(null)
const selectedCourseSlides = ref([])
const showCourseDetail = ref(false)


const newSlide = ref({ title: '', contentText: '', object3dUrl: '', position: 0 })

// ─── Nav ─────────────────────────────────────────────────
const iGrid    = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`
const iBook    = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>`
const iClass   = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>`
const iProfile = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`



const mainNav = computed(() => [
  { id: 'overview',   label: 'Vue globale', icon: iGrid },
  { id: 'profile',    label: 'Mon Profil',  icon: iProfile },
])
const contentNav = computed(() => [
  { id: 'courses',      label: 'Mes Cours',    icon: iBook,   badge: myCourses.value.length || null },
  { id: 'classroom',   label: 'Classrooms',   icon: iClass,  badge: myClassrooms.value.length || null },
])

// ─── Computed ─────────────────────────────────────────────
const teacherInitial = computed(() => currentTeacher.value.nom?.charAt(0)?.toUpperCase() || 'T')

const currentPageTitle = computed(() => ({
  overview: 'Vue globale', courses: 'Mes Cours',
  classroom: 'Classrooms', profile: 'Mon Profil'
})[activeView.value] || '')

const totalStudents = computed(() => {
  return new Set(classStudents.value.map(s => s.id)).size
})

const statsCards = computed(() => [
  { label: 'Mes cours',      val: myCourses.value.length,    trend: '', up: false, pct: 70, color: '#a78bfa', bg: 'rgba(167,139,250,0.1)', icon: iBook },
  { label: 'Mes classes',    val: myClassrooms.value.length, trend: '', up: false, pct: 50, color: '#34d399', bg: 'rgba(52,211,153,0.1)',  icon: iClass },
  { label: 'Total slides',   val: myCourses.value.reduce((a,c) => a + (c.slideCount||0), 0), trend: '', up: true, pct: 60, color: '#60a5fa', bg: 'rgba(96,165,250,0.1)', icon: iGrid },
  { label: 'Étudiants',      val: totalStudents.value,       trend: '', up: true,  pct: 80, color: '#f472b6', bg: 'rgba(244,114,182,0.1)', icon: iProfile },
])

const filteredCourses = computed(() => {
  if (!courseSearch.value) return myCourses.value
  return myCourses.value.filter(c =>
    c.title?.toLowerCase().includes(courseSearch.value.toLowerCase()))
})

const filteredClassrooms = computed(() => {
  if (!classSearch.value) return myClassrooms.value
  return myClassrooms.value.filter(c =>
    c.name?.toLowerCase().includes(classSearch.value.toLowerCase()))
})

function getCourseOfClass(cls) {
  return myCourses.value.find(c => c.id === cls?.course?.id || c.id === cls?.courseId)
}

// ─── Toast ────────────────────────────────────────────────
function showToast(msg, type = 'success') {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3000)
}

// ─── Load data ────────────────────────────────────────────
async function loadTeacher() {
  try {
    const res = await api.get('/users/me')
    currentTeacher.value = res.data
    profileForm.value = {
      nom: res.data.nom,
      email: res.data.email
    }
  } catch {
    // Fallback si endpoint pas encore disponible
    const email = localStorage.getItem('userName')
    currentTeacher.value = {
      nom: email?.split('@')[0] || 'Teacher',
      email: email || '',
      role: 'TEACHER'
    }
  }
}

async function loadCourses() {
  try {
    const res = await api.get('/courses')
    myCourses.value = res.data
  } catch (err) {
    console.log('loadCourses error:', err.response?.status)
  }
}
async function loadClassrooms() {
  try {
    const res = await api.get('/classrooms/my')
    myClassrooms.value = res.data
  } catch {
    // fallback
    try {
      const res = await api.get('/classrooms')
      myClassrooms.value = res.data
    } catch {}
  }
}

async function loadSlides(courseId) {
  try {
    const res = await api.get(`/courses/${courseId}/slides`)
    currentSlides.value = res.data
  } catch { currentSlides.value = [] }
}

async function loadClassStudents(cls) {
  try {
    const res = await api.get(`/classrooms/${cls.id}/students`)
    classStudents.value = res.data
  } catch { classStudents.value = [] }
}

// ─── QR Code (canvas based) ───────────────────────────────
function generateQR(text) {
  // Simple QR via API publique
  qrDataUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(text)}&bgcolor=0d1b2e&color=a78bfa&format=png`
}

// ─── Course actions ───────────────────────────────────────
function openCourseModal(mode, course = null) {
  courseModal.value = {
    show: true,
    mode,
    editId: course?.id || null,
    initialData: course || {}
  }
}

const activeSlide = ref(null)

function selectSlide(slide) {
  activeSlide.value = null  // ← reset d'abord
  setTimeout(() => {
    activeSlide.value = slide  // ← puis charge après 50ms
  }, 50)
}

function navigateSlide(direction) {
  const currentIndex = selectedCourseSlides.value.findIndex(s => s.id === activeSlide.value.id)
  const newIndex = currentIndex + direction
  if (newIndex >= 0 && newIndex < selectedCourseSlides.value.length) {
    activeSlide.value = selectedCourseSlides.value[newIndex]
  }
}


async function handleCourseSubmit(formData) {
  isLoading.value = true
  try {
    // Étape 1 — Créer le cours
    const courseRes = await api.post('/courses', {
      title: formData.title,
      description: formData.description
    })
    const courseId = courseRes.data.id

    // Étape 2 — Créer la première slide avec le modèle 3D
    if (formData.object3dUrl && courseId) {
      await api.post('/slides', {
        title: 'Slide 1 — ' + formData.title,
        contentText: formData.description || '',
        object3dUrl: formData.object3dUrl,
        position: 0,
        courseId: courseId
      })
    }

    showToast('Cours créé avec succès ! 🎉')
    courseModal.value.show = false
    await loadCourses()

  } catch (err) {
    console.log('Status:', err.response?.status)
    console.log('Data:', JSON.stringify(err.response?.data))
    showToast('Erreur lors de la création', 'error')
  } finally {
    isLoading.value = false
  }
}

async function saveCourse() {
  isLoading.value = true
  try {
    if (courseModal.value.mode === 'create') {
      await api.post('/courses', courseModal.value.form)
      showToast('Cours créé !')
    } else {
      await api.put(`/courses/${courseModal.value.editId}`, courseModal.value.form)
      showToast('Cours modifié !')
    }
    courseModal.value.show = false
    await loadCourses()
  } catch { showToast('Erreur', 'error') }
  finally { isLoading.value = false }
}

function confirmDeleteCourse(course) {
  deleteModal.value = {
    show: true, name: course.title,
    action: async () => {
      isLoading.value = true
      try {
        await api.delete(`/courses/${course.id}`)
        showToast('Cours supprimé !')
        deleteModal.value.show = false
        await loadCourses()
      } catch { showToast('Erreur', 'error') }
      finally { isLoading.value = false }
    }
  }
}

async function openCourseDetail(course) {
  selectedCourse.value = course
  activeSlide.value = null
  showCourseDetail.value = true
  try {
    const res = await api.get(`/courses/${course.id}/slides`)
    selectedCourseSlides.value = res.data
    if (res.data.length > 0) {
      // Délai pour que le modal soit rendu avant d'initialiser Three.js
      setTimeout(() => {
        activeSlide.value = res.data[0]
      }, 150)
    }
  } catch {
    selectedCourseSlides.value = []
  }
}

// ─── Slides actions ───────────────────────────────────────
async function openSlides(course) {
  slidesModal.value = { show: true, course }
  await loadSlides(course.id)
  newSlide.value = { title: '', contentText: '', object3dUrl: '', position: currentSlides.value.length }
}

async function addSlide() {
  if (!newSlide.value.title) { showToast('Titre requis', 'error'); return }
  isLoading.value = true
  try {
    await api.post('/slides', {
      ...newSlide.value,
      courseId: slidesModal.value.course.id
    })
    showToast('Slide ajoutée !')
    await loadSlides(slidesModal.value.course.id)
    newSlide.value = { title: '', contentText: '', object3dUrl: '', position: currentSlides.value.length }
    await loadCourses()
  } catch { showToast('Erreur ajout slide', 'error') }
  finally { isLoading.value = false }
}

async function deleteSlide(id) {
  try {
    await api.delete(`/slides/${id}`)
    showToast('Slide supprimée !')
    await loadSlides(slidesModal.value.course.id)
  } catch { showToast('Erreur', 'error') }
}

// ─── Classroom actions ────────────────────────────────────
function openClassModal(mode, cls = null) {
  classModal.value = {
    show: true, mode, editId: cls?.id || null,
    form: { name: cls?.name || '', courseId: cls?.course?.id || '' }
  }
}

async function saveClassroom() {
  isLoading.value = true
  try {
    const payload = {
      name: classModal.value.form.name,
      course: classModal.value.form.courseId ? { id: classModal.value.form.courseId } : null
    }
    if (classModal.value.mode === 'create') {
      await api.post('/classrooms', payload)
      showToast('Classe créée !')
    } else {
      await api.put(`/classrooms/${classModal.value.editId}`, payload)
      showToast('Classe modifiée !')
    }
    classModal.value.show = false
    await loadClassrooms()
  } catch { showToast('Erreur', 'error') }
  finally { isLoading.value = false }
}

async function openClassDetail(cls) {
  classDetailModal.value = { show: true, cls }
  generateQR(cls.inviteCode)
  await loadClassStudents(cls)
}

function showQR(cls) {
  qrModal.value = { show: true, cls }
  generateQR(cls.inviteCode)
}

function copyCode(code) {
  navigator.clipboard.writeText(code)
    .then(() => showToast('Code copié !'))
    .catch(() => showToast('Erreur copie', 'error'))
}

function confirmDeleteClass(cls) {
  deleteModal.value = {
    show: true, name: cls.name,
    action: async () => {
      isLoading.value = true
      try {
        await api.delete(`/classrooms/${cls.id}`)
        showToast('Classe supprimée !')
        deleteModal.value.show = false
        await loadClassrooms()
      } catch { showToast('Erreur', 'error') }
      finally { isLoading.value = false }
    }
  }
}

// ─── Profile ──────────────────────────────────────────────
function resetProfile() {
  profileForm.value = { nom: currentTeacher.value.nom, email: currentTeacher.value.email }
}

async function saveProfile() {
  isLoading.value = true
  try {
    await api.patch(`/users/${currentTeacher.value.id}`, profileForm.value)
    currentTeacher.value = { ...currentTeacher.value, ...profileForm.value }
    localStorage.setItem('userName', profileForm.value.email)
    showToast('Profil mis à jour !')
  } catch { showToast('Erreur', 'error') }
  finally { isLoading.value = false }
}

function logout() { localStorage.clear(); window.location.href = '/login' }

// ─── Mounted ──────────────────────────────────────────────
onMounted(async () => {
  await loadTeacher()
  await loadCourses()
  await loadClassrooms()
  const tick = () => {
    currentTime.value = new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
  }
  tick(); setInterval(tick, 60000)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap');
* { box-sizing: border-box; margin: 0; padding: 0; }

.teacher-root { display: flex; min-height: 100vh; background: #06091a; font-family: 'DM Sans', sans-serif; color: #fff; }

/* ── SIDEBAR ── */
.sidebar { width: 240px; min-height: 100vh; background: rgba(255,255,255,0.025); border-right: 1px solid rgba(167,139,250,0.1); display: flex; flex-direction: column; transition: width 0.3s cubic-bezier(.4,0,.2,1); position: sticky; top: 0; height: 100vh; overflow: hidden; }
.sidebar.collapsed { width: 64px; }
.sidebar-header { padding: 1.1rem 1rem; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.05); }
.brand { display: flex; align-items: center; gap: 9px; overflow: hidden; }
.brand-icon { width: 34px; height: 34px; background: rgba(167,139,250,0.1); border: 1px solid rgba(167,139,250,0.25); border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.brand-text { font-family: 'Syne', sans-serif; font-size: 1.05rem; font-weight: 800; white-space: nowrap; }
.brand-text em { color: #a78bfa; font-style: normal; }
.collapse-btn { background: none; border: none; cursor: pointer; color: rgba(255,255,255,0.3); padding: 4px; border-radius: 6px; transition: all 0.2s; flex-shrink: 0; }
.collapse-btn:hover { color: #fff; background: rgba(255,255,255,0.05); }

.teacher-badge { margin: 0.7rem 1rem; padding: 5px 10px; background: rgba(167,139,250,0.08); border: 1px solid rgba(167,139,250,0.18); border-radius: 20px; font-size: 0.72rem; color: #c4b5fd; display: flex; align-items: center; gap: 6px; width: fit-content; }
.badge-dot { width: 6px; height: 6px; border-radius: 50%; background: #a78bfa; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{ opacity:1; } 50%{ opacity:0.4; } }

.sidebar-nav { flex: 1; padding: 0.5rem 0; overflow-y: auto; }
.nav-section-label { padding: 0.4rem 1rem; font-size: 0.65rem; font-weight: 600; color: rgba(255,255,255,0.2); letter-spacing: 0.1em; text-transform: uppercase; }
.nav-item { width: 100%; display: flex; align-items: center; gap: 9px; padding: 0.6rem 1rem; background: none; border: none; cursor: pointer; color: rgba(255,255,255,0.4); font-family: 'DM Sans', sans-serif; font-size: 0.85rem; font-weight: 500; transition: all 0.2s; white-space: nowrap; overflow: hidden; position: relative; }
.nav-item:hover { color: #fff; background: rgba(167,139,250,0.05); }
.nav-item.active { color: #a78bfa; background: rgba(167,139,250,0.08); border-right: 2px solid #a78bfa; }
.nav-icon { flex-shrink: 0; display: flex; }
.nav-badge { margin-left: auto; background: rgba(167,139,250,0.15); color: #a78bfa; font-size: 0.68rem; padding: 2px 7px; border-radius: 10px; }

.sidebar-footer { padding: 0.9rem 1rem; border-top: 1px solid rgba(255,255,255,0.05); display: flex; align-items: center; gap: 8px; }
.teacher-info { display: flex; align-items: center; gap: 8px; flex: 1; overflow: hidden; }
.teacher-avatar-sm { width: 30px; height: 30px; border-radius: 50%; background: linear-gradient(135deg, #a78bfa, #7c3aed); display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 700; color: #fff; flex-shrink: 0; }
.teacher-name-sm { font-size: 0.8rem; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.teacher-role-sm { font-size: 0.68rem; color: rgba(255,255,255,0.3); }
.logout-btn { background: none; border: none; cursor: pointer; color: rgba(255,255,255,0.25); padding: 5px; border-radius: 7px; transition: all 0.2s; flex-shrink: 0; }
.logout-btn:hover { background: rgba(239,68,68,0.1); color: #f87171; }

/* ── MAIN ── */
.main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.topbar { display: flex; align-items: center; justify-content: space-between; padding: 1.1rem 2rem; border-bottom: 1px solid rgba(255,255,255,0.05); background: rgba(6,9,26,0.85); backdrop-filter: blur(10px); position: sticky; top: 0; z-index: 10; }
.page-title { font-family: 'Syne', sans-serif; font-size: 1.15rem; font-weight: 700; }
.breadcrumb { font-size: 0.72rem; color: rgba(255,255,255,0.28); margin-top: 2px; }
.topbar-right { display: flex; align-items: center; gap: 1rem; }
.search-box { display: flex; align-items: center; gap: 7px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.07); border-radius: 9px; padding: 0.45rem 0.75rem; }
.search-box input { background: none; border: none; outline: none; color: #fff; font-family: 'DM Sans', sans-serif; font-size: 0.83rem; width: 170px; }
.search-box input::placeholder { color: rgba(255,255,255,0.22); }
.search-box svg { color: rgba(255,255,255,0.28); flex-shrink: 0; }
.topbar-time { font-size: 0.8rem; color: rgba(255,255,255,0.28); font-variant-numeric: tabular-nums; }

/* ── VIEW ── */
.view { padding: 1.7rem 2rem; animation: fadeUp 0.35s ease; }
@keyframes fadeUp { from { opacity:0; transform:translateY(10px); } to { opacity:1; transform:none; } }

/* ── WELCOME ── */
.welcome-banner { background: linear-gradient(135deg, rgba(167,139,250,0.1), rgba(124,58,237,0.08), rgba(99,102,241,0.06)); border: 1px solid rgba(167,139,250,0.15); border-radius: 18px; padding: 1.8rem 2rem; display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.welcome-sub { font-size: 0.82rem; color: rgba(255,255,255,0.4); margin-bottom: 4px; }
.welcome-name { font-family: 'Syne', sans-serif; font-size: 1.6rem; font-weight: 800; margin-bottom: 8px; background: linear-gradient(90deg, #a78bfa, #c4b5fd); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.welcome-desc { font-size: 0.85rem; color: rgba(255,255,255,0.5); }
.welcome-desc strong { color: #a78bfa; }
.welcome-3d { opacity: 0.7; }

/* ── STATS ── */
.stats-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 1rem; margin-bottom: 1.5rem; }
.stat-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; padding: 1.1rem; animation: fadeUp 0.45s ease both; transition: transform 0.2s, border-color 0.2s; }
.stat-card:hover { transform: translateY(-2px); border-color: rgba(167,139,250,0.2); }
.stat-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.7rem; }
.stat-icon-wrap { width: 36px; height: 36px; border-radius: 9px; display: flex; align-items: center; justify-content: center; }
.trend { font-size: 0.72rem; font-weight: 500; }
.trend.up { color: #34d399; }
.trend.neutral { color: rgba(255,255,255,0.3); }
.stat-num { font-family: 'Syne', sans-serif; font-size: 1.7rem; font-weight: 700; margin-bottom: 2px; }
.stat-lbl { font-size: 0.76rem; color: rgba(255,255,255,0.38); margin-bottom: 0.7rem; }
.stat-bar { height: 3px; background: rgba(255,255,255,0.06); border-radius: 2px; }
.stat-bar-inner { height: 100%; border-radius: 2px; transition: width 0.8s ease; }

/* ── OVERVIEW ROW ── */
.overview-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.ov-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; padding: 1.2rem; }
.ov-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.ov-card-header h3 { font-family: 'Syne', sans-serif; font-size: 0.9rem; font-weight: 600; }
.btn-link { background: none; border: none; color: #a78bfa; cursor: pointer; font-size: 0.78rem; transition: opacity 0.2s; }
.btn-link:hover { opacity: 0.7; }

.course-mini-list { display: flex; flex-direction: column; gap: 6px; }
.course-mini { display: flex; align-items: center; gap: 10px; padding: 0.6rem 0.8rem; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.04); border-radius: 9px; cursor: pointer; transition: all 0.2s; }
.course-mini:hover { border-color: rgba(167,139,250,0.2); background: rgba(167,139,250,0.04); }
.course-mini-icon { font-size: 1.1rem; flex-shrink: 0; }
.course-mini-info { flex: 1; min-width: 0; }
.course-mini-title { font-size: 0.84rem; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.course-mini-desc { font-size: 0.73rem; color: rgba(255,255,255,0.35); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.course-mini-slides { font-size: 0.72rem; color: rgba(255,255,255,0.3); flex-shrink: 0; }

.class-mini-list { display: flex; flex-direction: column; gap: 6px; }
.class-mini { display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 0.8rem; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.04); border-radius: 9px; }
.class-mini-left { display: flex; align-items: center; gap: 8px; }
.class-mini-icon { font-size: 1.1rem; }
.class-mini-name { font-size: 0.84rem; font-weight: 500; }
.class-mini-code { font-size: 0.72rem; color: rgba(255,255,255,0.35); }
.class-mini-code strong { color: #a78bfa; }
.class-mini-count { font-size: 0.8rem; color: rgba(255,255,255,0.4); }

/* ── TOOLBAR ── */
.table-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.2rem; }
.search-inline { display: flex; align-items: center; gap: 7px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.07); border-radius: 9px; padding: 0.45rem 0.75rem; }
.search-inline svg { color: rgba(255,255,255,0.28); }
.search-inline input { background: none; border: none; outline: none; color: #fff; font-family: 'DM Sans', sans-serif; font-size: 0.83rem; width: 200px; }
.search-inline input::placeholder { color: rgba(255,255,255,0.22); }
.btn-primary { display: flex; align-items: center; gap: 5px; padding: 0.5rem 1.1rem; background: linear-gradient(135deg, #a78bfa, #7c3aed); border: none; border-radius: 9px; color: #fff; font-family: 'Syne', sans-serif; font-size: 0.83rem; font-weight: 600; cursor: pointer; transition: all 0.2s; min-width: 90px; justify-content: center; }
.btn-primary:hover { box-shadow: 0 6px 20px rgba(167,139,250,0.3); transform: translateY(-1px); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

/* ── COURSES GRID ── */
.courses-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px,1fr)); gap: 1rem; }
.course-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.07); border-radius: 14px; padding: 1.2rem; animation: fadeUp 0.4s ease both; transition: all 0.25s; }
.course-card:hover { transform: translateY(-3px); border-color: rgba(167,139,250,0.25); box-shadow: 0 8px 24px rgba(167,139,250,0.07); }
.course-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.8rem; }
.course-card-icon { font-size: 1.6rem; }
.course-card-actions { display: flex; gap: 4px; }
.course-card-title { font-family: 'Syne', sans-serif; font-size: 0.95rem; font-weight: 700; margin-bottom: 0.4rem; }
.course-card-desc { font-size: 0.78rem; color: rgba(255,255,255,0.38); line-height: 1.5; margin-bottom: 1rem; }
.course-card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 0.8rem; border-top: 1px solid rgba(255,255,255,0.06); }
.slide-count { font-size: 0.75rem; color: rgba(255,255,255,0.35); }
.btn-view-slides { font-size: 0.75rem; color: #a78bfa; background: none; border: none; cursor: pointer; }
.add-course-card { background: rgba(167,139,250,0.04); border: 2px dashed rgba(167,139,250,0.2); border-radius: 14px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; cursor: pointer; min-height: 160px; transition: all 0.2s; color: rgba(167,139,250,0.5); font-size: 0.85rem; }
.add-course-card:hover { border-color: rgba(167,139,250,0.45); background: rgba(167,139,250,0.07); color: #a78bfa; }
.add-icon { font-size: 2rem; line-height: 1; }

/* ── CLASSROOMS GRID ── */
.classrooms-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px,1fr)); gap: 1rem; }
.classroom-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.07); border-radius: 14px; padding: 1.2rem; cursor: pointer; animation: fadeUp 0.4s ease both; transition: all 0.25s; }
.classroom-card:hover { transform: translateY(-3px); border-color: rgba(52,211,153,0.25); box-shadow: 0 8px 24px rgba(52,211,153,0.07); }
.cls-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.8rem; }
.cls-avatar { font-size: 1.8rem; }
.cls-actions { display: flex; gap: 4px; }
.cls-name { font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700; margin-bottom: 6px; }
.cls-course-tag {
  font-size: 0.75rem;
  color: rgba(255,255,255,0.5);
  margin-bottom: 0.8rem;
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(167,139,250,0.06);
  border: 1px solid rgba(167,139,250,0.1);
  border-radius: 6px;
  padding: 4px 8px;
  width: fit-content;
}
.invite-section { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; padding: 0.7rem; margin-bottom: 0.8rem; display: flex; justify-content: space-between; align-items: center; gap: 8px; }
.invite-code-box { display: flex; align-items: center; gap: 8px; flex: 1; }
.invite-label { font-size: 0.65rem; color: rgba(255,255,255,0.3); white-space: nowrap; }
.invite-code { font-family: 'Syne', sans-serif; font-size: 0.95rem; font-weight: 700; color: #a78bfa; letter-spacing: 0.08em; }
.copy-btn { font-size: 0.68rem; padding: 3px 8px; background: rgba(167,139,250,0.1); border: 1px solid rgba(167,139,250,0.2); border-radius: 5px; color: #a78bfa; cursor: pointer; white-space: nowrap; transition: all 0.2s; }
.copy-btn:hover { background: rgba(167,139,250,0.18); }
.qr-btn { display: flex; align-items: center; gap: 4px; font-size: 0.72rem; padding: 5px 9px; background: rgba(52,211,153,0.08); border: 1px solid rgba(52,211,153,0.2); border-radius: 7px; color: #34d399; cursor: pointer; white-space: nowrap; transition: all 0.2s; }
.qr-btn:hover { background: rgba(52,211,153,0.14); }
.cls-footer { display: flex; justify-content: space-between; align-items: center; }
.cls-students { font-size: 0.75rem; color: rgba(255,255,255,0.35); }
.cls-detail-btn { font-size: 0.75rem; color: #34d399; }
.add-class-card { background: rgba(52,211,153,0.03); border: 2px dashed rgba(52,211,153,0.18); border-radius: 14px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; cursor: pointer; min-height: 200px; transition: all 0.2s; color: rgba(52,211,153,0.45); font-size: 0.85rem; }
.add-class-card:hover { border-color: rgba(52,211,153,0.4); color: #34d399; background: rgba(52,211,153,0.06); }

/* ── ICON BTNS ── */
.icon-btn { width: 28px; height: 28px; border-radius: 7px; border: none; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; }
.icon-btn.edit   { background: rgba(167,139,250,0.1); color: #a78bfa; }
.icon-btn.slides { background: rgba(96,165,250,0.1);  color: #60a5fa; }
.icon-btn.delete { background: rgba(239,68,68,0.1);   color: #f87171; }
.icon-btn:hover  { transform: scale(1.12); filter: brightness(1.2); }

/* ── EMPTY ── */
.empty-state { text-align: center; padding: 2rem; color: rgba(255,255,255,0.22); font-size: 0.84rem; }

/* ── MODALS ── */
.modal-overlay { position: fixed; inset: 0; background: rgba(6,9,26,0.88); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: #0e1228; border: 1px solid rgba(167,139,250,0.12); border-radius: 18px; width: 100%; max-width: 460px; animation: modalPop 0.3s cubic-bezier(0.34,1.56,0.64,1); overflow: hidden; }
.modal-sm { max-width: 360px; }
.modal-lg { max-width: 680px; }
@keyframes modalPop { from { opacity:0; transform:scale(0.88) translateY(20px); } to { opacity:1; transform:none; } }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 1.1rem 1.3rem; border-bottom: 1px solid rgba(255,255,255,0.06); }
.modal-header h3 { font-family: 'Syne', sans-serif; font-size: 0.95rem; font-weight: 600; }
.modal-close { background: none; border: none; cursor: pointer; color: rgba(255,255,255,0.3); font-size: 0.95rem; width: 26px; height: 26px; border-radius: 50%; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
.modal-close:hover { background: rgba(255,255,255,0.06); color: #fff; }
.modal-body { padding: 1.3rem; max-height: 75vh; overflow-y: auto; }
.modal-footer { display: flex; gap: 8px; justify-content: flex-end; padding: 1rem 1.3rem; border-top: 1px solid rgba(255,255,255,0.06); }
.modal-input { width: 100%; padding: 0.62rem 0.85rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.09); border-radius: 9px; color: #fff; font-family: 'DM Sans', sans-serif; font-size: 0.86rem; outline: none; transition: border-color 0.2s; }
.modal-input:focus { border-color: rgba(167,139,250,0.4); }
.modal-input::placeholder { color: rgba(255,255,255,0.2); }
select.modal-input { cursor: pointer; }
select.modal-input option { background: #0e1228; }
.modal-textarea { resize: vertical; min-height: 80px; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; font-size: 0.78rem; color: rgba(255,255,255,0.45); margin-bottom: 5px; font-weight: 500; }
.btn-cancel { padding: 0.55rem 1rem; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.09); border-radius: 9px; color: rgba(255,255,255,0.55); font-family: 'DM Sans', sans-serif; font-size: 0.85rem; cursor: pointer; transition: all 0.2s; }
.btn-cancel:hover { background: rgba(255,255,255,0.08); color: #fff; }
.btn-delete { padding: 0.55rem 1.1rem; background: linear-gradient(135deg,#ef4444,#dc2626); border: none; border-radius: 9px; color: #fff; font-family: 'Syne', sans-serif; font-size: 0.85rem; font-weight: 600; cursor: pointer; min-width: 85px; display: flex; align-items: center; justify-content: center; }

/* ── SLIDES MODAL ── */
.slide-add-form { background: rgba(167,139,250,0.04); border: 1px solid rgba(167,139,250,0.1); border-radius: 12px; padding: 1rem; margin-bottom: 1.2rem; }
.slide-form-grid { display: grid; grid-template-columns: 1fr auto; gap: 8px; }
.slides-list { display: flex; flex-direction: column; gap: 7px; }
.slide-item { display: flex; align-items: center; gap: 10px; padding: 0.65rem 0.9rem; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 9px; }
.slide-pos { font-family: 'Syne', sans-serif; font-size: 0.8rem; font-weight: 700; color: rgba(255,255,255,0.3); min-width: 24px; }
.slide-info { flex: 1; min-width: 0; }
.slide-title { font-size: 0.85rem; font-weight: 500; }
.slide-content { font-size: 0.73rem; color: rgba(255,255,255,0.35); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.slide-3d-tag { font-size: 0.68rem; padding: 2px 7px; background: rgba(96,165,250,0.1); border: 1px solid rgba(96,165,250,0.2); border-radius: 5px; color: #60a5fa; white-space: nowrap; }

/* ── CLASS DETAIL MODAL ── */
.cls-detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.detail-section { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 12px; padding: 1rem; }
.section-title { font-family: 'Syne', sans-serif; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.9rem; color: rgba(255,255,255,0.65); }
.big-code { font-family: 'Syne', sans-serif; font-size: 2rem; font-weight: 800; color: #a78bfa; letter-spacing: 0.12em; text-align: center; margin-bottom: 0.8rem; }
.copy-btn-lg { display: block; width: 100%; padding: 0.55rem; background: rgba(167,139,250,0.1); border: 1px solid rgba(167,139,250,0.25); border-radius: 9px; color: #a78bfa; font-size: 0.83rem; cursor: pointer; text-align: center; margin-bottom: 1rem; transition: all 0.2s; }
.copy-btn-lg:hover { background: rgba(167,139,250,0.18); }
.qr-container { text-align: center; }
.qr-img { width: 160px; height: 160px; border-radius: 12px; border: 2px solid rgba(167,139,250,0.2); margin: 0 auto 0.6rem; display: block; }
.qr-hint { font-size: 0.75rem; color: rgba(255,255,255,0.35); margin-bottom: 0.6rem; }
.qr-download { display: inline-block; font-size: 0.78rem; color: #34d399; text-decoration: none; padding: 5px 12px; background: rgba(52,211,153,0.08); border: 1px solid rgba(52,211,153,0.2); border-radius: 7px; transition: all 0.2s; }
.qr-download:hover { background: rgba(52,211,153,0.14); }
.students-scroll { max-height: 220px; overflow-y: auto; display: flex; flex-direction: column; gap: 6px; }
.student-row { display: flex; align-items: center; gap: 9px; padding: 0.55rem 0.7rem; background: rgba(255,255,255,0.02); border-radius: 8px; }
.s-avatar { width: 30px; height: 30px; border-radius: 50%; background: linear-gradient(135deg,#34d399,#059669); display: flex; align-items: center; justify-content: center; font-size: 0.78rem; font-weight: 700; color: #fff; flex-shrink: 0; }
.s-info { flex: 1; min-width: 0; }
.s-name { font-size: 0.84rem; font-weight: 500; }
.s-email { font-size: 0.72rem; color: rgba(255,255,255,0.32); }

/* ── PROFILE ── */
.profile-page { width: 100%; }
.profile-cover { height: 160px; border-radius: 16px; background: linear-gradient(135deg, rgba(167,139,250,0.18), rgba(124,58,237,0.12), rgba(99,102,241,0.08)); border: 1px solid rgba(167,139,250,0.12); position: relative; margin-bottom: 80px;      overflow: visible; }
.profile-cover-bg { position: absolute; inset: 0; background: radial-gradient(ellipse at 30% 50%, rgba(167,139,250,0.15) 0%, transparent 70%); }
.profile-avatar-wrap { position: absolute; bottom: -40px; bottom: -50px;  left: 2rem; }
.profile-ring {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  border: 3px solid #a78bfa;
  background: #0e1228;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: visible;        /* ← changer hidden en visible */
  flex-shrink: 0;
}
.profile-avatar-lg { font-family: 'Syne', sans-serif; font-size: 2rem; font-weight: 800; color: #a78bfa; }
.profile-content { display: grid; grid-template-columns: 240px 1fr; gap: 1.5rem; width: 100%; }
.profile-info-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.07); border-radius: 14px; padding: 1.4rem; text-align: center; }
.pi-avatar { width: 60px; height: 60px; border-radius: 50%; background: linear-gradient(135deg,#a78bfa,#7c3aed); display: flex; align-items: center; justify-content: center; font-family: 'Syne', sans-serif; font-size: 1.5rem; font-weight: 700; color: #fff; margin: 0 auto 0.8rem; }
.pi-name { font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700; margin-bottom: 3px; }
.pi-email { font-size: 0.78rem; color: rgba(255,255,255,0.38); margin-bottom: 0.7rem; }
.role-badge-teacher { font-size: 0.68rem; font-weight: 600; padding: 3px 9px; border-radius: 6px; background: rgba(167,139,250,0.12); color: #a78bfa; }
.pi-stats { display: flex; align-items: center; justify-content: center; gap: 1.2rem; margin-top: 1.1rem; padding-top: 0.9rem; border-top: 1px solid rgba(255,255,255,0.06); }
.pi-stat { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.pi-val { font-family: 'Syne', sans-serif; font-size: 1.1rem; font-weight: 700; color: #a78bfa; }
.pi-lbl { font-size: 0.68rem; color: rgba(255,255,255,0.3); }
.pi-div { width: 1px; height: 28px; background: rgba(255,255,255,0.08); }
.profile-edit-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.07); border-radius: 14px; padding: 1.5rem; width: 100%; }
.edit-title { font-family: 'Syne', sans-serif; font-size: 0.95rem; font-weight: 600; margin-bottom: 1.2rem; }
.input-row { display: flex; align-items: center; gap: 8px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.09); border-radius: 9px; padding: 0.62rem 0.85rem; transition: border-color 0.2s; }
.input-row:focus-within { border-color: rgba(167,139,250,0.4); }
.input-row svg { color: rgba(255,255,255,0.28); flex-shrink: 0; }
.t-input { background: none; border: none; outline: none; color: #fff; font-family: 'DM Sans', sans-serif; font-size: 0.86rem; flex: 1; }
.t-input::placeholder { color: rgba(255,255,255,0.2); }
.profile-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 1.2rem; }

/* ── SPINNER ── */
.spin { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.2); border-top-color: #fff; border-radius: 50%; animation: spin 0.7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ── TOAST ── */
.toast { position: fixed; bottom: 1.5rem; right: 1.5rem; padding: 0.7rem 1.1rem; border-radius: 11px; font-size: 0.83rem; z-index: 200; box-shadow: 0 8px 24px rgba(0,0,0,0.3); }
.toast.success { background: rgba(167,139,250,0.1); border: 1px solid rgba(167,139,250,0.25); color: #c4b5fd; }
.toast.error   { background: rgba(239,68,68,0.1);  border: 1px solid rgba(239,68,68,0.25);  color: #f87171; }

/* ── TRANSITIONS ── */
.fade-enter-active,.fade-leave-active { transition: all 0.25s ease; }
.fade-enter-from,.fade-leave-to { opacity:0; transform:translateY(8px); }
.modal-enter-active,.modal-leave-active { transition: all 0.25s ease; }
.modal-enter-from,.modal-leave-to { opacity:0; }
.toast-enter-active,.toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from,.toast-leave-to { opacity:0; transform:translateY(10px); }

/* COURSE DETAIL MODAL */
.modal-xl {
  max-width: 1100px;
  max-height: 90vh;
  width: 95vw;
}

.course-detail-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  height: 75vh;
  min-height: 550px;
  overflow: hidden;
}

/* SLIDES SIDEBAR */
.slides-sidebar {
  border-right: 1px solid rgba(255,255,255,0.06);
  display: flex; flex-direction: column;
  background: rgba(255,255,255,0.02);
}
.slides-sidebar-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0.9rem 1rem;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  font-size: 0.82rem; font-weight: 600; color: rgba(255,255,255,0.6);
}
.slides-sidebar-list { flex: 1; overflow-y: auto; padding: 0.5rem; display: flex; flex-direction: column; gap: 4px; }

.slide-thumb {
  display: flex; align-items: center; gap: 8px;
  padding: 0.6rem 0.7rem;
  border-radius: 8px; cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
}
.slide-thumb:hover { background: rgba(167,139,250,0.06); border-color: rgba(167,139,250,0.1); }
.slide-thumb.active { background: rgba(167,139,250,0.1); border-color: rgba(167,139,250,0.3); }
.slide-thumb-pos { font-size: 0.68rem; color: rgba(255,255,255,0.25); min-width: 16px; font-family: 'Syne', sans-serif; font-weight: 700; }
.slide-thumb-info { flex: 1; min-width: 0; }
.slide-thumb-title { font-size: 0.8rem; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.slide-thumb-preview { font-size: 0.7rem; color: rgba(255,255,255,0.3); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-top: 2px; }
.slide-thumb-3d { font-size: 0.8rem; flex-shrink: 0; }

/* COURSE DETAIL MAIN */
.course-detail-main {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #060918;
}
.course-detail-empty { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 2rem; }


.slide-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.slide-3d-area {
  flex: 1;
  min-height: 0;
  height: 100%;
  width: 100%;
  position: relative;
  background: #060918;
}
.slide-3d-area :deep(.three-container) {
  height: 100% !important;
  min-height: 100% !important;
  border-radius: 0 !important;
}

.slide-3d-area :deep(canvas) {
  width: 100% !important;
  height: 100% !important;
}


.slide-info-panel {
  padding: 1rem 1.5rem;
  border-top: 1px solid rgba(255,255,255,0.06);
  background: rgba(6,9,26,0.8);
  flex-shrink: 0;
}
.slide-info-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
.slide-info-title { font-family: 'Syne', sans-serif; font-size: 1rem; font-weight: 700; }
.slide-info-pos { font-size: 0.75rem; color: rgba(255,255,255,0.3); }
.slide-info-content { font-size: 0.85rem; color: rgba(255,255,255,0.5); line-height: 1.6; margin-bottom: 1rem; }

/* SLIDE NAVIGATION */
.slide-nav { display: flex; align-items: center; justify-content: space-between; }
.slide-nav-btn {
  padding: 6px 14px; background: rgba(167,139,250,0.1);
  border: 1px solid rgba(167,139,250,0.2); border-radius: 8px;
  color: #a78bfa; font-size: 0.8rem; cursor: pointer; transition: all 0.2s;
}
.slide-nav-btn:hover:not(:disabled) { background: rgba(167,139,250,0.2); }
.slide-nav-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.slide-dots { display: flex; gap: 6px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(255,255,255,0.15); cursor: pointer; transition: all 0.2s; }
.dot.active { background: #a78bfa; transform: scale(1.2); }

/* ── RESPONSIVE ── */
@media (max-width: 1100px) { .stats-grid { grid-template-columns: repeat(2,1fr); } .profile-content { grid-template-columns: 1fr; } }
@media (max-width: 768px) { .overview-row { grid-template-columns: 1fr; } .cls-detail-grid { grid-template-columns: 1fr; } }
</style>