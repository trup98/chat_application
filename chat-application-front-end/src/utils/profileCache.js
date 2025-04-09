const PROFILE_CACHE_KEY = "user-profile-cache";
const EXPIRATION_DURATION = 60 * 60 * 1000; // 1 hour

export const getCachedProfileUrl = (userId) => {
    const rawCache = localStorage.getItem(PROFILE_CACHE_KEY);
    if (!rawCache) return null;

    const cache = JSON.parse(rawCache);
    const entry = cache[userId];

    if (!entry) return null;

    const now = Date.now();
    if (now - entry.timestamp > EXPIRATION_DURATION) {
        return null; // Expired
    }

    return entry.url;
};

export const saveProfileUrlToCache = (userId, url) => {
    const rawCache = localStorage.getItem(PROFILE_CACHE_KEY);
    const cache = rawCache ? JSON.parse(rawCache) : {};

    cache[userId] = {
        url,
        timestamp: Date.now(),
    };

    localStorage.setItem(PROFILE_CACHE_KEY, JSON.stringify(cache));
};

export const cleanupProfileCache = () => {
    const rawCache = localStorage.getItem(PROFILE_CACHE_KEY);
    if (!rawCache) return;

    const cache = JSON.parse(rawCache);
    const now = Date.now();

    Object.keys(cache).forEach(key => {
        if (now - cache[key].timestamp > EXPIRATION_DURATION) {
            delete cache[key];
        }
    });

    localStorage.setItem(PROFILE_CACHE_KEY, JSON.stringify(cache));
};
