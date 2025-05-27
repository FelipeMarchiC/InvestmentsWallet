import * as React from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import '../AssetCard/AssetCard.css';
import { FaChartLine } from 'react-icons/fa';
import { investmentService } from '../../services/investmentService';
import { Alert } from '@mui/material';
import Snackbar from '@mui/material/Snackbar';

export default function CreateInvestmentDialog({assetId}) {
  const [openDialog, setOpenDialog] = React.useState(false);

  const [snackbarOpen, setSnackbarOpen] = React.useState(false);
  const [snackbarMessage, setSnackbarMessage] = React.useState('');
  const [snackbarSeverity, setSnackbarSeverity] = React.useState('success');

 const handleClickOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSnackbarClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarOpen(false);
  };

  return (
    <React.Fragment>
      <button className="invest-button" onClick={handleClickOpenDialog}>
        <FaChartLine /> Investir
      </button>
      <Dialog
        open={openDialog}
        onClose={handleCloseDialog}
        slotProps={{
          paper: {
            component: 'form',
            onSubmit: async (event) => {
              event.preventDefault();
              const formData = new FormData(event.currentTarget);
              const formJson = Object.fromEntries(formData.entries());
              const initialValue = formJson.initialValue;
              try {
                var res = await investmentService.registerInvestment(initialValue, assetId);
                if (res && (res.status === 201 || res.status === 200)) {
                  setSnackbarMessage('Investimento registrado com sucesso!');
                  setSnackbarSeverity('success');
                  setSnackbarOpen(true);
                  handleCloseDialog();
                } else {
                  const errorMessage = error.response?.data?.message || error.message;
                  console.log(errorMessage);
                  setSnackbarMessage('Erro ao registrar investimento.');
                  setSnackbarSeverity('error');
                  setSnackbarOpen(true);
                }
                handleCloseDialog();
              } catch (error) {
                const errorMessage = error.response?.data?.message || error.message;
                console.log(errorMessage);
                setSnackbarMessage('Falha ao registrar investimento. Tente novamente.');
                setSnackbarSeverity('error');
                setSnackbarOpen(true);
              }
            },
          },
        }}
      >
        <DialogTitle>Registrar um investimento</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Para registrar um investimento, por favor, insira o valor inicial do investimento.
          </DialogContentText>
          <TextField
            autoFocus
            required
            margin="dense"
            id="initialValue"
            name="initialValue"
            label="Valor inicial"
            type="number"
            min="1"
            fullWidth
            variant="standard"
            InputProps={{
              inputProps: {
                step: '0.1',
                min: '1',
              },
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancelar</Button>
          <Button type="submit">Registrar</Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000} // Tempo em ms que o snackbar fica visível
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </React.Fragment>
  );
}