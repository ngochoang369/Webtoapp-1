-- ==========================================
-- SUPABASE SQL SCHEMA FOR VAULT DIARY APP
-- Mở Supabase Dashboard -> SQL Editor -> Paste & Run
-- ==========================================

-- 1. Bảng lưu trữ nhật ký mã hóa E2EE (diaries)
CREATE TABLE IF NOT EXISTS public.diaries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT NOT NULL,
    user_email TEXT NOT NULL,
    title_encrypted TEXT NOT NULL,
    content_encrypted TEXT NOT NULL,
    mood VARCHAR(50),
    tags TEXT,
    weather VARCHAR(50),
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_e2ee_encrypted BOOLEAN DEFAULT TRUE,
    allow_admin_audit BOOLEAN DEFAULT TRUE,
    synced_to_supabase BOOLEAN DEFAULT TRUE
);

-- Index cho truy vấn nhanh
CREATE INDEX IF NOT EXISTS idx_diaries_user_email ON public.diaries(user_email);
CREATE INDEX IF NOT EXISTS idx_diaries_created_at ON public.diaries(created_at DESC);

-- Bật Row Level Security (RLS) cho bảng diaries
ALTER TABLE public.diaries ENABLE ROW LEVEL SECURITY;

-- Policy: Người dùng chỉ có thể đọc nhật ký của chính mình
CREATE POLICY "Users can select own diaries"
ON public.diaries
FOR SELECT
USING (auth.jwt() ->> 'email' = user_email OR user_email = 'devregish@gmail.com');

-- Policy: Người dùng chỉ có thể chèn nhật ký của chính mình
CREATE POLICY "Users can insert own diaries"
ON public.diaries
FOR INSERT
WITH CHECK (auth.jwt() ->> 'email' = user_email);

-- Policy: Người dùng có thể sửa nhật ký của chính mình
CREATE POLICY "Users can update own diaries"
ON public.diaries
FOR UPDATE
USING (auth.jwt() ->> 'email' = user_email);

-- Policy: Người dùng có thể xóa nhật ký của chính mình
CREATE POLICY "Users can delete own diaries"
ON public.diaries
FOR DELETE
USING (auth.jwt() ->> 'email' = user_email);


-- 2. Bảng nhật ký kiểm toán Admin (audit_logs)
CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timestamp BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint,
    admin_email TEXT NOT NULL,
    target_user_email TEXT NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    reason TEXT,
    entry_id TEXT
);

-- Index cho nhật ký kiểm toán
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON public.audit_logs(timestamp DESC);

-- Bật RLS cho audit_logs
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;

-- Policy: Chỉ Admin devregish@gmail.com mới có quyền xem & ghi log kiểm toán
CREATE POLICY "Admin full access to audit_logs"
ON public.audit_logs
FOR ALL
USING (auth.jwt() ->> 'email' = 'devregish@gmail.com' OR admin_email = 'devregish@gmail.com');