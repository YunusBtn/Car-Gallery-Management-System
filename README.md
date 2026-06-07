<div align="center">

<img src="https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
<img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-Security-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
<img src="https://img.shields.io/badge/Tests-60%20Passed-brightgreen?style=for-the-badge&logo=junit5&logoColor=white"/>

# 🚗 Car Gallery Management System

**Gerçek zamanlı TCMB döviz kuru entegrasyonu ile tam kapsamlı araç galerisi yönetim sistemi.**

[Özellikler](#-özellikler) • [Mimari](#-mimari) • [API Dokümantasyonu](#-api-dokümantasyonu) • [Kurulum](#-kurulum) • [Testler](#-testler) • [Teknolojiler](#-teknolojiler)

</div>

---

## 📖 Proje Hakkında

**Car Gallery Management System**, araç galericilerin envanterlerini yönetebileceği, müşterilerin araç satın alabileceği ve tüm işlemlerin **gerçek zamanlı TCMB (Türkiye Cumhuriyet Merkez Bankası) USD kuru** üzerinden hesaplandığı full-stack bir web uygulamasıdır.

Sistem; galerici, müşteri ve yönetici rollerini desteklemekte, JWT tabanlı kimlik doğrulama ve Google OAuth2 entegrasyonu sunmaktadır.

### 🎯 Temel İş Akışı

```
Müşteri araç satın almak ister
       ↓
Sistem TCMB API'den güncel USD kurunu çeker
       ↓
Müşteri'nin TL bakiyesi USD'ye çevrilir
       ↓
Araç fiyatı (USD) ile karşılaştırılır
       ↓
Yeterli bakiye varsa → Satış gerçekleşir
Araç durumu SOLD olarak güncellenir
Kalan bakiye TL olarak hesaplanıp yazılır
```

---

## ✨ Özellikler

| Modül | Özellikler |
|---|---|
| 🔐 **Kimlik Doğrulama** | JWT token, kayıt/giriş, Google OAuth2, rol tabanlı yetkilendirme |
| 🚗 **Araç Yönetimi** | Araç ekleme, listeleme, detay görüntüleme, silme, durum takibi (SALABLE / DAMAGED / SOLD) |
| 👤 **Müşteri Yönetimi** | Müşteri kaydı, profil görüntüleme, hesap (bakiye) yönetimi |
| 🏪 **Galerici Yönetimi** | Galerici kaydı, araç-galerici ilişkisi, profil görüntüleme |
| 💰 **Araç Satışı** | Gerçek zamanlı TCMB döviz kuru entegrasyonu, USD/TL dönüşümü, otomatik bakiye güncelleme |
| 📍 **Adres Yönetimi** | Şehir, ilçe, mahalle, sokak bilgisi yönetimi |
| 📊 **Swagger UI** | Tüm API endpoint'leri için interaktif dokümantasyon |
| 🐳 **Docker** | Tek komutla çalışan containerized ortam |

---

## 🏗️ Mimari

```
galeri_project/
├── backend/                          # Spring Boot 3.2.2
│   └── src/main/java/com/yunus/
│       ├── controller/               # REST Controller'lar (8 adet)
│       ├── service/                  # İş Mantığı Katmanı (11 servis)
│       ├── repository/               # Spring Data JPA Repository'ler (9 adet)
│       ├── mapper/                   # MapStruct Mapper'lar (7 adet)
│       ├── model/                    # JPA Entity'ler (9 adet)
│       ├── dto/                      # Data Transfer Object'ler
│       ├── exception/                # Global exception handling
│       ├── enums/                    # CarStatusType, CurrencyType
│       └── utils/                    # Yardımcı sınıflar (DateUtils)
│
└── frontend/                         # React 19 + Vite
    └── src/
        ├── pages/                    # Sayfa bileşenleri (7 sayfa)
        ├── components/               # Ortak bileşenler (Modal, Sidebar)
        └── services/                 # API servis katmanı
```

### 📊 Entity İlişki Diyagramı

```
User ──────────── Role (ManyToMany)
 │
 ├── Customer (OneToOne)
 │       ├── Account (bakiye - TL)
 │       └── Address
 │
 └── Gallerist (OneToOne)
         └── Address

Car ───────────── GalleristCar ──── Gallerist
 │                (araç-galerici)
 └── SoldCar ──── Customer
      │       └── Gallerist
      └── (satış kaydı)
```

---

## 🛠️ Teknolojiler

### Backend
| Teknoloji | Versiyon | Kullanım Amacı |
|---|---|---|
| **Java** | 17 | Ana programlama dili |
| **Spring Boot** | 3.2.2 | Uygulama framework'ü |
| **Spring Security** | 6.x | JWT kimlik doğrulama + OAuth2 |
| **Spring Data JPA** | 3.x | ORM ve veritabanı işlemleri |
| **PostgreSQL** | 15 | Ana veritabanı |
| **MapStruct** | 1.5.5 | Entity-DTO dönüşümleri |
| **Lombok** | 1.18.32 | Boilerplate kod azaltma |
| **JJWT** | 0.12.3 | JWT token üretimi/doğrulaması |
| **SpringDoc OpenAPI** | 2.2.0 | Swagger UI dokümantasyonu |
| **JUnit 5 + Mockito** | - | Unit testler (60 test) |

### Frontend
| Teknoloji | Versiyon | Kullanım Amacı |
|---|---|---|
| **React** | 19 | UI framework'ü |
| **Vite** | 8 | Build aracı |
| **React Router DOM** | 7 | Client-side routing |

### Altyapı
| Teknoloji | Kullanım Amacı |
|---|---|
| **Docker + Docker Compose** | Containerized deployment |
| **TCMB EVDS API** | Gerçek zamanlı USD/TL kuru |
| **Google OAuth2** | Sosyal giriş desteği |

---

## 📡 API Dokümantasyonu

Uygulama çalıştığında Swagger UI'ya erişin:
```
http://localhost:8081/swagger-ui/index.html
```

### Endpoint Özeti

#### 🔐 Auth (`/api/auth`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/register` | Yeni kullanıcı kaydı | ❌ |
| `POST` | `/login` | Kullanıcı girişi, JWT döner | ❌ |

#### 🚗 Car (`/api/car`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/save` | Yeni araç ekle | ✅ |
| `GET` | `/list` | Tüm araçları listele | ✅ |
| `GET` | `/{id}` | ID ile araç getir | ✅ |
| `DELETE` | `/{id}` | Araç sil | ✅ |

#### 👤 Customer (`/api/customer`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/save` | Müşteri profili oluştur | ✅ |
| `GET` | `/list` | Tüm müşterileri listele | ✅ |
| `GET` | `/{id}` | ID ile müşteri getir | ✅ |
| `GET` | `/my-profile` | Oturum açan müşterinin profili | ✅ |

#### 🏪 Gallerist (`/api/gallerist`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/save` | Galerici profili oluştur | ✅ |
| `GET` | `/list` | Tüm galeristeleri listele | ✅ |
| `GET` | `/{id}` | ID ile galerici getir | ✅ |
| `GET` | `/my-profile` | Oturum açan galeristin profili | ✅ |

#### 🔗 Gallerist-Car (`/api/gallerist-car`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/save` | Galericiye araç ata | ✅ |
| `GET` | `/list` | Tüm galerici-araç ilişkilerini listele | ✅ |

#### 💰 Sold Car (`/api/sold-car`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/buy` | **Araç satın al** (TCMB kur entegrasyonu) | ✅ |
| `GET` | `/list` | Tüm satışları listele | ✅ |

#### 📍 Address (`/api/address`) & 🏦 Account (`/api/account`)
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `POST` | `/save` | Yeni kayıt oluştur | ✅ |
| `GET` | `/{id}` | ID ile kayıt getir | ✅ |

---

## 🚀 Kurulum

### Ön Gereksinimler
- Docker & Docker Compose
- Java 17+ (sadece yerel geliştirme için)
- Node.js 18+ (sadece frontend geliştirme için)

### ⚡ Hızlı Başlangıç (Docker ile)

**1. Repoyu klonla**
```bash
git clone https://github.com/YunusBtn/Car-Gallery-Management-System.git
cd Car-Gallery-Management-System/backend
```

**2. `.env` dosyasını oluştur**
```bash
cp .env.example .env
```

`.env` dosyasını düzenle:
```env
# Veritabanı
POSTGRES_DB=gallery_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password

# JWT
JWT_SECRET=your_base64_encoded_secret_key_min_256_bit
JWT_EXPIRATION=86400000

# TCMB API (https://evds2.tcmb.gov.tr adresinden alın)
TCMB_KEY=your_tcmb_api_key

# Google OAuth2 (isteğe bağlı)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

**3. Docker Compose ile başlat**
```bash
docker-compose up --build
```

✅ Backend → `http://localhost:8081`
✅ Swagger UI → `http://localhost:8081/swagger-ui/index.html`

---

### 🔧 Yerel Geliştirme

#### Backend
```bash
cd backend

# Ortam değişkenlerini ayarla
# Windows (PowerShell):
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/gallery_db"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:JWT_SECRET="your_secret"
$env:JWT_EXPIRATION="86400000"
$env:TCMB_KEY="your_tcmb_key"

# Çalıştır
.\mvnw.cmd spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend → `http://localhost:5173`

---

## 🧪 Testler

Proje **60 unit test** içermektedir. Tüm testler JUnit 5 + Mockito ile yazılmıştır ve veritabanı bağlantısı gerektirmez.

```bash
cd backend
.\mvnw.cmd test
```

### Test Sonuçları

```
Tests run: 62, Failures: 0, Errors: 0, Skipped: 2
BUILD SUCCESS
```

| Test Sınıfı | Test Sayısı | Kapsam |
|---|---|---|
| `SoldCarServiceTest` | **13** | Araç satış iş akışı, TCMB entegrasyonu, bakiye hesaplama |
| `CustomerServiceTest` | **11** | Müşteri kayıt, profil, SecurityContext mock |
| `GalleristServiceTest` | **10** | Galerici kayıt, profil, SecurityContext mock |
| `JwtServiceTest` | **7** | Token üretimi, doğrulama, süre kontrolü |
| `CarServiceTest` | **6** | Araç CRUD işlemleri |
| `GalleristCarServiceTest` | **5** | Araç-galerici ilişki yönetimi |
| `AccountServiceTest` | **4** | Hesap kayıt ve getirme |
| `AddressServiceTest` | **4** | Adres kayıt ve getirme |

> **Not:** `GaleriProjectApplicationTests` — PostgreSQL bağlantısı gerektiren integration test olup `@Disabled` ile işaretlenmiştir. `docker-compose up` sonrası aktif edilebilir.

---

## 🔐 Rol Sistemi

```
ADMIN     → Tüm yetkiler
MANAGER   → Yönetim yetkileri
USER      → Temel kayıt (varsayılan)
CUSTOMER  → Müşteri profili oluşturulunca eklenir
GALLERIST → Galerici profili oluşturulunca eklenir
```

> ADMIN ve MANAGER rolleri kayıt sırasında atanamaz, güvenlik önlemi olarak engellenmiştir.

---

## 💱 TCMB Kur Entegrasyonu

Araç satış sürecinde güncel USD/TL kuru otomatik olarak **TCMB EVDS API**'den çekilir:

```
TCMB EVDS API → USD/TL Kuru → Müşteri TL Bakiyesi → USD'ye Çevir → Araç Fiyatı (USD) ile Karşılaştır
```

- **Veri Kaynağı:** `https://evds2.tcmb.gov.tr/service/evds`
- **Seri:** `TP.DK.USD.A` (Dolar Alış)
- **Güncellik:** Her satış işleminde anlık çekim

---

## 📁 Ortam Değişkenleri

| Değişken | Açıklama | Zorunlu |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL bağlantı URL'i | ✅ |
| `SPRING_DATASOURCE_USERNAME` | Veritabanı kullanıcısı | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | Veritabanı şifresi | ✅ |
| `JWT_SECRET` | JWT imzalama anahtarı (Base64, min 256-bit) | ✅ |
| `JWT_EXPIRATION` | Token geçerlilik süresi (ms) | ✅ |
| `TCMB_KEY` | TCMB EVDS API anahtarı | ✅ |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Client ID | ⬜ |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 Client Secret | ⬜ |

---

## 🗂️ Proje Yapısı (Detaylı)

```
galeri_project/
│
├── backend/
│   ├── src/main/java/com/yunus/
│   │   ├── controller/
│   │   │   ├── AccountController.java
│   │   │   ├── AddressController.java
│   │   │   ├── AuthController.java
│   │   │   ├── CarController.java
│   │   │   ├── CustomerController.java
│   │   │   ├── GalleristCarController.java
│   │   │   ├── GalleristController.java
│   │   │   └── SoldCarController.java
│   │   ├── service/
│   │   │   ├── AccountService.java
│   │   │   ├── AddressService.java
│   │   │   ├── AuthService.java
│   │   │   ├── CarService.java
│   │   │   ├── CurrencyRateService.java      ← TCMB API
│   │   │   ├── CustomerService.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── GalleristCarService.java
│   │   │   ├── GalleristService.java
│   │   │   ├── JwtService.java
│   │   │   └── SoldCarService.java           ← Ana iş mantığı
│   │   ├── model/
│   │   │   ├── Account.java
│   │   │   ├── Address.java
│   │   │   ├── BaseEntity.java
│   │   │   ├── Car.java
│   │   │   ├── Customer.java
│   │   │   ├── Gallerist.java
│   │   │   ├── GalleristCar.java
│   │   │   ├── Role.java
│   │   │   ├── SoldCar.java
│   │   │   ├── User.java
│   │   │   └── UserPrincipal.java
│   │   └── ...
│   ├── src/test/java/com/yunus/service/      ← 60 Unit Test
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── pom.xml
│
└── frontend/
    └── src/
        ├── pages/
        │   ├── LoginPage.jsx
        │   ├── RegisterPage.jsx
        │   ├── DashboardPage.jsx
        │   ├── CarsPage.jsx
        │   ├── CustomersPage.jsx
        │   ├── SalesPage.jsx
        │   ├── BuyCarPage.jsx
        │   └── CompleteProfilePage.jsx
        ├── components/
        │   ├── Sidebar.jsx
        │   └── Modal.jsx
        └── services/
            └── api.js
```

---

## 🤝 Katkı Sağlama

1. Fork'la
2. Feature branch oluştur: `git checkout -b feature/yeni-ozellik`
3. Değişiklikleri commit'le: `git commit -m 'feat: yeni özellik eklendi'`
4. Branch'i push'la: `git push origin feature/yeni-ozellik`
5. Pull Request aç

---

## 👤 Geliştirici

**Yunus Emre Bütün**

[![GitHub](https://img.shields.io/badge/GitHub-YunusBtn-181717?style=for-the-badge&logo=github)](https://github.com/YunusBtn)

---

<div align="center">

⭐ Bu projeyi beğendiyseniz star vermeyi unutmayın!

</div>
