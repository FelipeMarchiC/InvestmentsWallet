import * as React from "react";
import Button from "@mui/material/Button";
import ClickAwayListener from "@mui/material/ClickAwayListener";
import Grow from "@mui/material/Grow";
import Paper from "@mui/material/Paper";
import Popper from "@mui/material/Popper";
import MenuItem from "@mui/material/MenuItem";
import MenuList from "@mui/material/MenuList";
import { RiAccountPinCircleFill } from "react-icons/ri";
import { IoIosArrowDown } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import "./MenuListComposition.css";

export default function MenuListComposition() {
  const [open, setOpen] = React.useState(false);
  const anchorRef = React.useRef(null);
  const navigate = useNavigate();

  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen);
  };

  const handleClose = (event) => {
    if (anchorRef.current && anchorRef.current.contains(event.target)) {
      return;
    }

    setOpen(false);
  };

  const handleLogout = async (event) => {
    event.preventDefault();
    handleClose(event);
    localStorage.clear();
    navigate("/login");
  };

  function handleListKeyDown(event) {
    if (event.key === "Tab") {
      event.preventDefault();
      setOpen(false);
    } else if (event.key === "Escape") {
      setOpen(false);
    }
  }

  // return focus to the button when we transitioned from !open -> open
  const prevOpen = React.useRef(open);
  React.useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current.focus();
    }

    prevOpen.current = open;
  }, [open]);

  return (
    <div>
      <Button
        ref={anchorRef}
        id="composition-button"
        aria-controls={open ? "composition-menu" : undefined}
        aria-expanded={open ? "true" : undefined}
        aria-haspopup="true"
        onClick={handleToggle}
      >
        <div className="menu-list-open">
          R$ 0,00
          <RiAccountPinCircleFill size={36} color="#4574f7" />
          <IoIosArrowDown />
        </div>
      </Button>
      <Popper
        open={open}
        anchorEl={anchorRef.current}
        role={undefined}
        placement="bottom-start"
        transition
        disablePortal
      >
        {({ TransitionProps, placement }) => (
          <Grow
            {...TransitionProps}
            style={{
              transformOrigin:
                placement === "bottom-start" ? "left top" : "left bottom",
            }}
          >
            <Paper>
              <ClickAwayListener onClickAway={handleClose}>
                <MenuList
                  autoFocusItem={open}
                  id="composition-menu"
                  aria-labelledby="composition-button"
                  onKeyDown={handleListKeyDown}
                  sx={{ padding: 0 }}
                  className="menu-list"
                >
                  <MenuItem
                    sx={{ paddingRight: 10 }}
                    className="user-info"
                    onClick={handleClose}
                  >
                    <div style={{ display: "flex", flexDirection: "column" }}>
                      <div>
                        <b>Nome</b>
                      </div>
                      <div style={{ fontSize: 14 }}>email@gmail.com</div>
                    </div>
                  </MenuItem>
                  <MenuItem sx={{ paddingRight: 10 }} onClick={handleClose}>
                    Minha conta
                  </MenuItem>
                  <MenuItem sx={{ paddingRight: 10 }} onClick={handleLogout}>
                    Sair
                  </MenuItem>
                </MenuList>
              </ClickAwayListener>
            </Paper>
          </Grow>
        )}
      </Popper>
    </div>
  );
}
