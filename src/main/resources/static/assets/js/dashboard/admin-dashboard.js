document.addEventListener("DOMContentLoaded", function() {
    const blocks = document.querySelectorAll('.admin-dashboard .modules-block');
    const isTouchDevice = 'ontouchstart' in window || navigator.maxTouchPoints > 0;
    blocks.forEach(block => {
        let timeout;
        if (isTouchDevice) {
            block.addEventListener('click', (e) => {
                e.preventDefault();
                blocks.forEach(b => {
                    if (b !== block) b.classList.remove('open');
                });
                block.classList.toggle('open');
            });
        } else {
            block.addEventListener('mouseenter', () => {
                clearTimeout(timeout);
                block.classList.add('open');
            });
            block.addEventListener('mouseleave', () => {
                block.classList.remove('open');
            });
        }
    });
});