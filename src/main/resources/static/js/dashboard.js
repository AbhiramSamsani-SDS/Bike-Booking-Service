const API = '/bike';

const user = JSON.parse(localStorage.getItem('bikeUser'));
const password = sessionStorage.getItem('bikeUserPassword');

if (!user || !password) {
    location = 'index.html';
}

let ridePage = 0;
let txPage = 0;
let rideLast = false;
let txLast = false;
let moneyLockInterval = null;

const $ = id => document.getElementById(id);


// ======================================================
// COMMON FUNCTIONS
// ======================================================

async function data(res) {
    const t = await res.text();

    try {
        return JSON.parse(t);
    } catch {
        return t;
    }
}

function toast(message) {
    $('toast').textContent = message;
    $('toast').classList.add('show');

    setTimeout(() => {
        $('toast').classList.remove('show');
    }, 2500);
}


// ======================================================
// PROFILE
// ======================================================

function profile() {
    $('welcomeUser').textContent = user.userName;

    $('profileAvatar').textContent =
        (user.userName || 'U')[0].toUpperCase();

    if (user.profilePhoto) {
        $('profileAvatar').style.backgroundImage =
            `url(${user.profilePhoto})`;

        $('profileAvatar').textContent = '';
    }
}


$('profileButton').addEventListener('click', e => {

    if (!e.target.closest('.profile-menu')) {
        $('profileMenu').classList.toggle('hidden');
    }
});


document.addEventListener('click', e => {

    if (!e.target.closest('#profileButton')) {
        $('profileMenu').classList.add('hidden');
    }
});


$('photoInput').addEventListener('change', e => {

    const f = e.target.files[0];

    if (!f) {
        return;
    }

    if (f.size > 1_500_000) {
        toast('Choose an image smaller than 1.5 MB');
        return;
    }

    const r = new FileReader();

    r.onload = async () => {

        const res = await fetch(
            `${API}/user/profile-photo/${user.userId}`,
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    profilePhoto: r.result
                })
            }
        );

        const d = await data(res);

        if (res.ok) {

            localStorage.setItem(
                'bikeUser',
                JSON.stringify(d)
            );

            location.reload();

        } else {
            toast(d.message || 'Photo update failed');
        }
    };

    r.readAsDataURL(f);
});


// ======================================================
// DASHBOARD
// ======================================================

async function refresh() {

    const [b, s] = await Promise.all([

        fetch(
            `${API}/userLogin/checkBalance/${user.userId}`
        ),

        fetch(
            `${API}/user/stats/${user.userId}`
        )
    ]);

    if (b.ok) {

        $('balance').textContent =
            `₹${Number(await data(b)).toFixed(2)}`;
    }

    if (s.ok) {

        const x = await data(s);

        $('totalRides').textContent =
            x.totalRides;

        $('availableDrivers').textContent =
            x.availableDrivers;
    }
}


function setAmount(v) {
    $('amount').value = v;
}


// ======================================================
// ADD MONEY LOCK TIMER
// ======================================================

function startLockTimer(
    elementId,
    lockedUntil,
    label
) {

    clearInterval(moneyLockInterval);

    const el = $(elementId);

    el.classList.remove('hidden');

    const end =
        new Date(lockedUntil).getTime();


    const tick = () => {

        const ms =
            end - Date.now();

        if (ms <= 0) {

            clearInterval(
                moneyLockInterval
            );

            el.classList.add(
                'hidden'
            );

            $('moneyMessage').textContent =
                'You can try again now.';

            return;
        }


        const total =
            Math.ceil(ms / 1000);

        const h =
            Math.floor(total / 3600);

        const m =
            Math.floor(
                (total % 3600) / 60
            );

        const s =
            total % 60;


        el.textContent =
            `${label} available in ` +
            `${String(h).padStart(2, '0')}:` +
            `${String(m).padStart(2, '0')}:` +
            `${String(s).padStart(2, '0')}`;
    };


    tick();

    moneyLockInterval =
        setInterval(tick, 1000);
}


// ======================================================
// MONEY CHALLENGE
// ======================================================

async function loadMoneyChallenge() {

    const r = await fetch(
        `${API}/user/add-money/challenge/${user.userId}`
    );

    const d = await data(r);


    if (!r.ok) {

        $('moneyMessage').textContent =
            d.message ||
            'Challenge unavailable';

        return false;
    }


    if (d.locked) {

        $('moneyQuestion').textContent =
            'Security challenge locked';

        $('moneyAttempts').textContent =
            'Maximum incorrect attempts reached.';

        startLockTimer(
            'moneyLockTimer',
            d.lockedUntil,
            'Add money'
        );

        return false;
    }


    $('moneyLockTimer')
        .classList.add('hidden');


    $('moneyQuestion').textContent =
        d.question;


    $('moneyAttempts').textContent =
        `Attempts remaining: ${d.remainingAttempts}`;


    return true;
}


async function openMoneyModal() {

    if (
        Number(
            $('amount').value
        ) <= 0
    ) {

        toast(
            'Enter an amount first'
        );

        return;
    }


    $('moneyModal')
        .classList.remove('hidden');


    $('moneyMessage').textContent = '';


    await loadMoneyChallenge();
}


function closeMoneyModal() {

    $('moneyModal')
        .classList.add('hidden');

    clearInterval(
        moneyLockInterval
    );
}


async function verifyAddMoney() {

    const r = await fetch(
        `${API}/user/add-money/verify`,
        {
            method: 'POST',

            headers: {
                'Content-Type':
                    'application/json'
            },

            body: JSON.stringify({

                userId:
                    user.userId,

                answer:
                    Number(
                        $('moneyAnswer')
                            .value
                    ),

                amount:
                    Number(
                        $('amount')
                            .value
                    )
            })
        }
    );


    const d = await data(r);


    if (!r.ok) {

        $('moneyMessage').textContent =
            d.message ||
            'Verification failed';

        await loadMoneyChallenge();

        return;
    }


    closeMoneyModal();

    $('amount').value = '';
    $('moneyAnswer').value = '';

    toast(
        'Money added successfully'
    );

    refresh();
    loadTx();
}


// ======================================================
// BOOK RIDE
// ======================================================

async function bookRide() {

    const from =
        $('boarding').value.trim();

    const to =
        $('destination').value.trim();


    if (!from || !to) {

        $('rideMessage').textContent =
            'Enter pickup and destination';

        return;
    }


    const r = await fetch(
        `${API}/userLogin/bookRide`,
        {
            method: 'POST',

            headers: {
                'Content-Type':
                    'application/json'
            },

            body: JSON.stringify({

                userId:
                    user.userId,

                password:
                    password,

                boarding:
                    from,

                destination:
                    to
            })
        }
    );


    const d = await data(r);


    if (!r.ok) {

        $('rideMessage').textContent =
            d.message ||
            'Unable to book ride';

        return;
    }


    $('rideMessage').style.color =
        '#20853b';


    $('rideMessage').textContent =
        `Ride #${d.rideId} booked • ` +
        `Driver ${d.driver?.driverName || ''} • ` +
        `Fare ₹${d.fare}`;


    toast('Ride booked');


    refresh();

    ridePage = 0;
    txPage = 0;

    loadRides();
    loadTx();
}


// ======================================================
// RIDE TIMER
// ======================================================

function rideTimer(r) {

    if (
        r.rideStatus !== 'ONGOING' ||
        !r.estimatedCompletionTime
    ) {
        return '';
    }


    return `
        <span
            class="ride-timer"
            data-ride-end="${r.estimatedCompletionTime}">
            ⏱ Calculating…
        </span>
    `;
}


function updateRideTimers() {

    document
        .querySelectorAll(
            '[data-ride-end]'
        )
        .forEach(el => {

            const sec = Math.max(
                0,
                Math.ceil(
                    (
                        new Date(
                            el.dataset.rideEnd
                        ).getTime()
                        -
                        Date.now()
                    ) / 1000
                )
            );


            if (sec <= 0) {

                el.textContent =
                    '✓ Completing ride…';

                el.classList.add(
                    'done'
                );

            } else {

                const m =
                    Math.floor(sec / 60);

                const s =
                    sec % 60;


                el.textContent =
                    `⏱ ${
                        m > 0
                            ? m + 'm '
                            : ''
                    }${s}s remaining`;
            }
        });
}


// ======================================================
// RIDE ROW
// ======================================================

function rideRow(r) {

    return `
        <div class="list-item">

            <div>

                <strong>
                    ${r.boarding}
                    →
                    ${r.destination}
                </strong>

                <small>
                    Ride #${r.rideId}
                    •
                    ${r.driver?.driverName || 'Driver'}
                </small>

                ${rideTimer(r)}

            </div>

            <div>

                <strong>
                    ₹${Number(r.fare).toFixed(2)}
                </strong>

                <span class="badge">
                    ${r.rideStatus}
                </span>

            </div>

        </div>
    `;
}


// ======================================================
// TRANSACTION ROW
// ======================================================

function txRow(t) {

    const c =
        t.transactionType === 'CREDIT';


    return `
        <div class="list-item">

            <div>

                <strong>
                    ${
                        c
                            ? 'Wallet top-up'
                            : 'Ride payment'
                    }
                </strong>

                <small>
                    Transaction #${t.transactionId}
                    ${
                        t.ride
                            ? ' • Ride #' +
                              t.ride.rideId
                            : ''
                    }
                </small>

            </div>

            <strong
                class="${
                    c
                        ? 'amount-credit'
                        : 'amount-debit'
                }">

                ${c ? '+' : '-'}
                ₹${Number(t.amount).toFixed(2)}

            </strong>

        </div>
    `;
}


// ======================================================
// LOAD RIDES
// ======================================================

async function loadRides() {

    const r = await fetch(
        `${API}/userLogin/rideHistory/${user.userId}` +
        `?page=${ridePage}&size=5`
    );


    const d = await data(r);


    if (!r.ok) {

        $('rideHistory').innerHTML =
            '<div class="empty">No rides yet.</div>';

        rideLast = true;

    } else {

        $('rideHistory').innerHTML =
            d.content.length
                ? d.content
                    .map(rideRow)
                    .join('')
                : '<div class="empty">No rides on this page.</div>';


        rideLast = d.last;

        updateRideTimers();
    }


    $('ridePageLabel').textContent =
        `Page ${ridePage + 1}`;


    $('ridePrev').disabled =
        ridePage === 0;


    $('rideNext').disabled =
        rideLast;
}


// ======================================================
// LOAD TRANSACTIONS
// ======================================================

async function loadTx() {

    const r = await fetch(
        `${API}/userLogin/transactionHistory/${user.userId}` +
        `?page=${txPage}&size=5`
    );


    const d = await data(r);


    if (!r.ok) {

        $('transactionHistory').innerHTML =
            '<div class="empty">No transactions yet.</div>';

        txLast = true;

    } else {

        $('transactionHistory').innerHTML =
            d.content.length
                ? d.content
                    .map(txRow)
                    .join('')
                : '<div class="empty">No transactions on this page.</div>';


        txLast = d.last;
    }


    $('txPageLabel').textContent =
        `Page ${txPage + 1}`;


    $('txPrev').disabled =
        txPage === 0;


    $('txNext').disabled =
        txLast;
}


// ======================================================
// PAGINATION
// ======================================================

function changeRidePage(n) {

    if (
        ridePage + n < 0 ||
        (n > 0 && rideLast)
    ) {
        return;
    }

    ridePage += n;

    loadRides();
}


function changeTxPage(n) {

    if (
        txPage + n < 0 ||
        (n > 0 && txLast)
    ) {
        return;
    }

    txPage += n;

    loadTx();
}


// ======================================================
// LOGOUT
// ======================================================

function logout() {

    localStorage.removeItem(
        'bikeUser'
    );

    sessionStorage.removeItem(
        'bikeUserPassword'
    );

    location = 'index.html';
}


// ======================================================
// DELETE ACCOUNT
// ======================================================

async function deleteAccount() {

    if (
        !confirm(
            'Delete your account? You will no longer be able to login.'
        )
    ) {
        return;
    }


    const r = await fetch(
        `${API}/user/${user.userId}`,
        {
            method: 'DELETE'
        }
    );


    const d = await data(r);


    if (!r.ok) {

        toast(
            d.message ||
            'Unable to delete account'
        );

        return;
    }


    logout();
}


// ======================================================
// PAGE START
// ======================================================

profile();

refresh();

loadRides();

loadTx();


// Update ride countdown every second
setInterval(
    updateRideTimers,
    1000
);


// Refresh dashboard every 8 seconds
setInterval(() => {

    refresh();

    loadRides();

    loadTx();

}, 8000);