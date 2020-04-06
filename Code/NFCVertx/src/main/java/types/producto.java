package types;

public class producto {
	
	//private int idProducto;
	private String nombreProducto;
	private Long codigoBarras;
	private String fabricante;
	private Long telefono;
	
	
	public producto() {
		super();
		this.nombreProducto = null;
		this.codigoBarras = null;
		this.fabricante = null;
		this.telefono = null;
	}
	
	public producto(String nombreProducto, Long codigoBarras, String fabricante, long telefono) {
		super();
		this.nombreProducto = nombreProducto;
		this.codigoBarras = codigoBarras;
		this.fabricante = fabricante;
		this.telefono = telefono;
	}
	
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) throws Exception {
		if(nombreProducto.length()<=45) {
			this.nombreProducto = nombreProducto;
		}else {
			throw new Exception("El nombre del producto debe contener como maximo 45 caracteres");
		}
		
	}
	public Long getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(Long codigoBarras) throws Exception {
		if(String.valueOf(codigoBarras).length()<=45) {
			this.codigoBarras = codigoBarras;
		}else {
			throw new Exception("El codigo de barras debe contener como maximo 45 digitos");
		}
		
	}
	public String getFabricante() {
		return fabricante;
		
	}
	public void setFabricante(String fabricante) throws Exception {
		if(String.valueOf(fabricante).length()<=45) {
			this.fabricante = fabricante;
		}else {
			throw new Exception("El fabricante debe contener como maximo 45 digitos");
		}
	}
	public Long getTelefono() {
		return telefono;
	}
	public void setTelefono(Long telefono) throws Exception {
		if(String.valueOf(telefono).length() == 9) {
			this.telefono = telefono;
		}else {
			throw new Exception("El numero de telefono debe contener 9 digitos");
		}
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (codigoBarras ^ (codigoBarras >>> 32));
		result = prime * result + ((fabricante == null) ? 0 : fabricante.hashCode());
		result = prime * result + ((nombreProducto == null) ? 0 : nombreProducto.hashCode());
		result = prime * result + (int) (telefono ^ (telefono >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		producto other = (producto) obj;
		if (codigoBarras != other.codigoBarras)
			return false;
		if (fabricante == null) {
			if (other.fabricante != null)
				return false;
		} else if (!fabricante.equals(other.fabricante))
			return false;
		if (nombreProducto == null) {
			if (other.nombreProducto != null)
				return false;
		} else if (!nombreProducto.equals(other.nombreProducto))
			return false;
		if (telefono != other.telefono)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "producto [nombreProducto=" + nombreProducto + ", codigoBarras=" + codigoBarras + ", fabricante="
				+ fabricante + ", telefono=" + telefono + "]";
	}
	
	
}
