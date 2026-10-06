const API = '/bike';

const driver = JSON.parse(localStorage.getItem('bikeDriver'));

if (!driver) {
    location = 'index.html';
}

let ridePage = 0;
let txPage = 0;
let rideLast = false;
let txLast = false;
let withdrawLockInterval = null;

const $ = id => document.getElementById(id);


// ======================================================
// COMMON FUNCTIONS
// ======================================================

async function data(r) {

    const t = await r.text();

    try {
        return JSON.parse(t);
    } catch {
        return t;
    }
}


function toast(m) {

    $('toast').textContent = m;

    $('toast').classList.add('show');

    setTimeout(() => {
        $('toast').classList.remove('show');
    }, 2500);
}


// ======================================================
// PROFILE
// ======================================================

function profile() {

    $('welcomeDriver').textContent =
        driver.driverName;

    $('profileAvatar').textContent =
        (driver.driverName || 'D')[0].toUpperCase();

    if (driver.profilePhoto) {

        $('profileAvatar').style.backgroundImage =
            `url(${driver.profilePhoto})`;

        $('profileAvatar').textContent = '';
    }
}


// ======================================================
// PROFILE MENU
// ======================================================

$('profileButton').addEventListener('click', e => {

    if (!e.target.closest('.profile-menu')) {

        $('profileMenu')
            .classList.toggle('hidden');
    }
});


document.addEventListener('click', e => {

    if (!e.target.closest('#profileButton')) {

        $('profileMenu')
            .classList.add('hidden');
    }
});


// ======================================================
// PROFILE PHOTO
// ======================================================

$('photoInput').addEventListener('change', e => {

    const f = e.target.files[0];

    if (!f) {
        return;
    }

    if (f.size > 1_500_000) {

        toast(
            'Choose an image smaller than 1.5 MB'
        );

        return;
    }


    const rd = new FileReader();


    rd.onload = async () => {

        const r = await fetch(
            `${API}/driver/profile-photo/${driver.driverId}`,
            {
                method: 'PUT',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify({
                    profilePhoto: rd.result
                })
            }
        );


        const d = await data(r);


        if (r.ok) {

            localStorage.setItem(
                'bikeDriver',
                JSON.stringify(d)
            );

            location.reload();

        } else {

            toast(
                d.message ||
                'Photo update failed'
            );
        }
    };


    rd.readAsDataURL(f);
});


// ======================================================
// DASHBOARD
// ======================================================

async function refresh() {

    const [b, s] = await Promise.all([

        fetch(
            `${API}/driverLogin/checkBalance/${driver.driverId}`
        ),

        fetch(
            `${API}/driver/stats/${driver.driverId}`
        )
    ]);


    if (b.ok) {

        $('driverBalance').textContent =
            `₹${Number(await data(b)).toFixed(2)}`;
    }


    if (s.ok) {

        const x = await data(s);

        $('totalRides').textContent =
            x.totalRides;

        $('driverStatus').textContent =
            x.available
                ? 'Available'
                : 'On ride';

        $('availabilityBadge').textContent =
            x.available
                ? '● Available for rides'
                : '● Currently on a ride';
    }
}


// ======================================================
// WITHDRAW LOCK TIMER
// ======================================================

function startWithdrawLockTimer(lockedUntil) {

    clearInterval(withdrawLockInterval);


    const el =
        $('withdrawLockTimer');

    el.classList.remove('hidden');


    const end =
        new Date(lockedUntil).getTime();


    const tick = () => {

        const ms =
            end - Date.now();


        if (ms <= 0) {

            clearInterval(
                withdrawLockInterval
            );

            el.classList.add(
                'hidden'
            );

            $('withdrawMessage').textContent =
                'You can try withdrawing again now.';

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
            `Withdraw available in ` +
            `${String(h).padStart(2, '0')}:` +
            `${String(m).padStart(2, '0')}:` +
            `${String(s).padStart(2, '0')}`;
    };


    tick();

    withdrawLockInterval =
        setInterval(tick, 1000);
}


// ======================================================
// WITHDRAW CHALLENGE
// ======================================================

async function loadWithdrawChallenge() {

    const r = await fetch(
        `${API}/driver/withdraw/challenge/${driver.driverId}`
    );

    const d = await data(r);


    if (!r.ok) {

        $('withdrawMessage').textContent =
            d.message ||
            'Challenge unavailable';

        return false;
    }


    if (d.locked) {

        $('withdrawQuestion').textContent =
            'Security challenge locked';

        $('withdrawAttempts').textContent =
            'Maximum incorrect attempts reached.';

        startWithdrawLockTimer(
            d.lockedUntil
        );

        return false;
    }


    $('withdrawLockTimer')
        .classList.add('hidden');


    $('withdrawQuestion').textContent =
        d.question;


    $('withdrawAttempts').textContent =
        `Attempts remaining: ${d.remainingAttempts}`;


    return true;
}


// ======================================================
// OPEN / CLOSE WITHDRAW MODAL
// ======================================================

async function openWithdrawModal() {

    if (
        Number(
            $('withdrawAmount').value
        ) <= 0
    ) {

        toast(
            'Enter withdrawal amount first'
        );

        return;
    }


    $('withdrawModal')
        .classList.remove('hidden');


    $('withdrawMessage').textContent = '';


    await loadWithdrawChallenge();
}


function closeWithdrawModal() {

    $('withdrawModal')
        .classList.add('hidden');

    clearInterval(
        withdrawLockInterval
    );
}


// ======================================================
// VERIFY WITHDRAW
// ======================================================

async function verifyWithdraw() {

    const r = await fetch(
        `${API}/driver/withdraw/verify`,
        {
            method: 'POST',

            headers: {
                'Content-Type': 'application/json'
            },

            body: JSON.stringify({

                driverId:
                    driver.driverId,

                answer:
                    Number(
                        $('withdrawAnswer').value
                    ),

                amount:
                    Number(
                        $('withdrawAmount').value
                    )
            })
        }
    );


    const d = await data(r);


    if (!r.ok) {

        $('withdrawMessage').textContent =
            d.message ||
            'Verification failed';

        await loadWithdrawChallenge();

        return;
    }


    closeWithdrawModal();

    $('withdrawAmount').value = '';

    $('withdrawAnswer').value = '';

    toast(
        'Withdrawal successful'
    );

    refresh();

    txPage = 0;

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
                    ${r.user?.userName || 'User'}
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
                            ? 'Ride earning'
                            : 'Withdrawal'
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
// UPDATE RIDE TIMERS
// ======================================================

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
// LOAD RIDES
// ======================================================

async function loadRides() {

    const r = await fetch(
        `${API}/driverLogin/rideHistory/${driver.driverId}` +
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
        `${API}/driverLogin/transactionHistory/${driver.driverId}` +
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
        'bikeDriver'
    );

    location = 'index.html';
}


// ======================================================
// DELETE DRIVER ACCOUNT
// ======================================================

async function deleteAccount() {

    if (
        !confirm(
            'Delete your driver account?'
        )
    ) {
        return;
    }


    const r = await fetch(
        `${API}/driver/account/${driver.driverId}`,
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