$(document).ready(function() {
    const path = window.location.pathname; // Get current URL path
    // Check if we're in the "Home" section
    if (path === contextPath + '/index') {
        $('#homeNav').addClass('active'); // Highlight Home
    } else {
        $('#modulesNav').addClass('active'); // Highlight Modules
    }

    $('#dynamic-search-input').on('input', function() {
        filterMegaMenu('');
    });

    $('#dynamic-search-input-report').on('input', function() {
        filterMegaMenu('-report');
    });
});

function filterMegaMenu(suffix) {
    const $dynamicSearchBox = $('#dynamic-search-input' + suffix);
    const $dynamicMenu = $('#dynamic-mega-menu' + suffix);
    const searchTerm = $dynamicSearchBox.val().toLowerCase();
    const $menuSections = $dynamicMenu.find('.menu-section' + suffix);
    $menuSections.each(function() {
        const $section = $(this);
        const $title = $section.find('.title');
        const $menuLinks = $section.find('a');
        let isSectionVisible = isSubSectionAvailable($menuLinks, searchTerm, false);
        if (isSectionVisible) {
            $title.show();
            $section.show();
        } else {
            $title.hide();
            $section.hide();
        }
    });
}

function isSubSectionAvailable($menuLinks, searchTerm, isSectionVisible) {
    $menuLinks.each(function() {
        const $link = $(this);
        const itemText = $link.text().toLowerCase();

        if (itemText.includes(searchTerm)) {
            $link.show();
            isSectionVisible = true;
        } else {
            let subLinks = $link.siblings('ul').find('a');
            if (subLinks.length > 0) {
                let isSectionVisible2 = isSubSectionAvailable(subLinks, searchTerm, isSectionVisible)
                if (isSectionVisible2) {
                    $link.show();
                    isSectionVisible = true;
                    expandSubMenu($link.attr('id'), true);
                }
                else {
                    $link.hide();
                    isSectionVisible = false;
                }
            } else {
                $link.hide();
            }
        }
    });
    return isSectionVisible;
}

function expandSubMenu(menuElemId, forceShow) {
    const subMenuId = $('#' + menuElemId.replace('menu_', 'submenu_'));
    if (subMenuId.length > 0) {
        if (forceShow || subMenuId.hasClass("d-none")) {
            subMenuId.removeClass("d-none");
        } else {
            subMenuId.addClass("d-none");
        }
    }
}