# Nhật Ký Bảo Mật Vault & E2EE - Android App

Ứng dụng nhật ký bảo mật cá nhân mã hóa đầu-cuối (End-to-End Encryption) trên Android, hỗ trợ mã hóaAES-256-GCM, mở khóa bằng PIN/Sinh trắc học.

---

## 🚀 Tự Động Build APK bằng GitHub Actions

Dự án đã được cấu hình sẵn **GitHub Actions** tại thư mục `.github/workflows/android.yml`.

### Các bước đẩy code lên GitHub & Tải file APK:

1. **Khởi tạo và đẩy code lên GitHub:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit - Vault Diary E2EE"
   git branch -M main
   git remote add origin https://github.com/TÊN_USER/TÊN_REPO.git
   git push -u origin main
   ```

2. **Tự động Build APK:**
   - Khi bạn `push` code lên nhánh `main`, GitHub Actions sẽ tự động thực hiện build dự án.
   - Bạn có thể theo dõi tiến trình tại tab **Actions** trên Repository GitHub của bạn.

3. **Tải về File APK:**
   - Vào tab **Actions** -> Chọn workflow vừa chạy thành công.
   - Cuộn xuống phần **Artifacts** để tải về file **`VaultDiary-Debug-APK`**.

---

## 🛠 Hướng Dẫn Build Thủ Công Local

Chạy lệnh sau tại thư mục gốc của dự án:

```bash
./gradlew assembleDebug
```

File APK đầu ra nằm tại: `app/build/outputs/apk/debug/app-debug.apk`
