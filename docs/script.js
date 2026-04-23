const navLinks = document.querySelectorAll(".side-nav a");
const sections = document.querySelectorAll("main section[id]");
const revealItems = document.querySelectorAll(".reveal");

// mikro scroll animation gia na fainetai pio presentation-like to report site
const revealObserver = new IntersectionObserver(
    (entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("is-visible");
            }
        });
    },
    {
        threshold: 0.14
    }
);

revealItems.forEach((item) => revealObserver.observe(item));

// active state sto navigation menu analoga me to poio section vlepei o xristis
const sectionObserver = new IntersectionObserver(
    (entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) {
                return;
            }

            const currentId = entry.target.getAttribute("id");

            navLinks.forEach((link) => {
                link.classList.toggle("active", link.getAttribute("href") === `#${currentId}`);
            });
        });
    },
    {
        rootMargin: "-25% 0px -55% 0px",
        threshold: 0.1
    }
);

sections.forEach((section) => sectionObserver.observe(section));
