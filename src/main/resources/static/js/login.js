const API = '/bike';

let role = 'user';


// ======================================================
// SELECT USER / DRIVER ROLE
// ======================================================

function selectRole(r) {

    role = r;

    document
        .getElementById('userTab')
        .classList.toggle('active', r === 'user');

    document
        .getElementById('driverTab')
        .classList.toggle('active', r === 'driver');

    document.getElementById('idLabel').textContent =
        r === 'user' ? 'User ID' : 'Driver ID';

    document.getElementById('roleEyebrow').textContent =
        r === 'user' ? 'USER ACCESS' : 'DRIVER PARTNER';

    document.getElementById('loginSub').textContent =
        r === 'user'
            ? 'Login to book your next ride.'
            : 'Login to manage rides and earnings.';

    document.getElementById('registerTitle').textContent =
        `Create ${r} account`;

    showLogin();
}


// ======================================================
// SHOW REGISTER PAGE
// ======================================================

function showRegister() {

    document
        .getElementById('loginView')
        .classList.add('hidden');

    document
        .getElementById('registerView')
        .classList.remove('hidden');
}


// ======================================================
// SHOW LOGIN PAGE
// ======================================================

function showLogin() {

    document
        .getElementById('registerView')
        .classList.add('hidden');

    document
        .getElementById('loginView')
        .classList.remove('hidden');
}


// ======================================================
// CONVERT RESPONSE TO JSON
// ======================================================

async function json(res) {

    const t = await res.text();

    try {
        return JSON.parse(t);
    } catch {
        return t;
    }
}


// ======================================================
// LOGIN
// ======================================================

document
    .getElementById('loginForm')
    .addEventListener('submit', async e => {

        e.preventDefault();

        const id = Number(loginId.value);

        const password =
            loginPassword.value;


        const body =
            role === 'user'
                ? {
                    userId: id,
                    password: password
                }
                : {
                    driverId: id,
                    password: password
                };


        const res = await fetch(
            `${API}/${role}Login`,
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify(body)
            }
        );


        const data =
            await json(res);


        if (!res.ok) {

            loginMessage.textContent =
                data.message ||
                'Login failed';

            return;
        }


        // USER LOGIN
        if (role === 'user') {

            localStorage.setItem(
                'bikeUser',
                JSON.stringify(data)
            );

            sessionStorage.setItem(
                'bikeUserPassword',
                password
            );

            location =
                'dashboard.html';

        }

        // DRIVER LOGIN
        else {

            localStorage.setItem(
                'bikeDriver',
                JSON.stringify(data)
            );

            location =
                'driver-dashboard.html';
        }
    });


// ======================================================
// REGISTER
// ======================================================

document
    .getElementById('registerForm')
    .addEventListener('submit', async e => {

        e.preventDefault();


        const name =
            registerName.value.trim();

        const email =
            registerEmail.value.trim();

        const password =
            registerPassword.value;


        const body =
            role === 'user'
                ? {
                    userName: name,
                    email: email,
                    password: password
                }
                : {
                    driverName: name,
                    email: email,
                    password: password
                };


        const res = await fetch(
            `${API}/accounts/${role}`,
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify(body)
            }
        );


        const data =
            await json(res);


        if (!res.ok) {

            registerMessage.textContent =
                data.message ||
                'Registration failed';

            return;
        }


        const id =
            role === 'user'
                ? data.userId
                : data.driverId;


        registerMessage.style.color =
            '#20853b';


        registerMessage.textContent =
            `Account created. Your ${role} ID is ${id}. ` +
            `Save this ID and login.`;
    });