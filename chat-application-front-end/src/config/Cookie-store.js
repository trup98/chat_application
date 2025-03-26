import Cookies from "js-cookie";

// set cookies
export const setCookie = (name, value, attributes = {}) => {
    Cookies.set(name, value, attributes);
}

export const setUserId = (name, value) => {
    Cookies.set(name, value);
}

export const getUserId = (name) => {
    return Cookies.get(name);
}
// set Role to the cookies
export const setRoleToCookies = (name, value, attributes = {}) => {
    Cookies.set(name, value, attributes);
}

export const getTokenFromCookie = (name) => {
    return Cookies.get(name);
}

export const setUserName = (name, value) => {
    Cookies.set(name, value);
}

export const getUserName = (name) => {
    return Cookies.get(name);
}
// get role from cookies
export const getRoleFromCookie = (name) => {
    return Cookies.get(name);
}
export const removeUserSession = () =>{
    Cookies.remove("token");
    Cookies.remove("userName");
    Cookies.remove("role");
    Cookies.remove("userId");
}


export const getCookie = (key) => {
    if (key !== null && key !== undefined) {
        return Cookies.get(key);
    } else {
        return Cookies.get();
    }
};

export const fourteenMinutesTime = () => {
    // change expiry time and then check user logs out automatically or not
    return new Date(new Date().getTime() + 14 * 60 * 1000);
};


// isLoggedIn
export const isAuthenticated = () => {
    let token = Cookies.get("token");
    return !(token == null && token === undefined);
}

export const isRoleAuthenticated = () => {
    const role = getRoleFromCookie("role")
    return role === 'ROLE_ADMIN';
}